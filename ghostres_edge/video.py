from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path
import time

import cv2

from .engines.base import EnhancementEngine


@dataclass(frozen=True)
class VideoResult:
    input_resolution: str
    output_resolution: str
    fps: float
    total_frames: int
    processed_frames: int
    processing_seconds: float


def enhance_video(
    *,
    input_path: Path,
    output_path: Path,
    engine: EnhancementEngine,
    codec: str = "mp4v",
) -> VideoResult:
    input_path = input_path.resolve()
    output_path = output_path.resolve()

    if not input_path.is_file():
        raise FileNotFoundError(f"Input video not found: {input_path}")
    if output_path.exists():
        raise FileExistsError(
            f"Output already exists: {output_path}\n"
            "Choose a new output filename so we never overwrite a result."
        )

    cap = cv2.VideoCapture(str(input_path))
    if not cap.isOpened():
        raise RuntimeError(f"Could not open video: {input_path}")

    fps = cap.get(cv2.CAP_PROP_FPS)
    total_frames = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
    width = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
    height = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))

    if fps <= 0 or width <= 0 or height <= 0:
        cap.release()
        raise RuntimeError("Could not read valid video metadata.")

    output_width = width * engine.scale
    output_height = height * engine.scale

    writer = cv2.VideoWriter(
        str(output_path),
        cv2.VideoWriter_fourcc(*codec),
        fps,
        (output_width, output_height),
    )
    if not writer.isOpened():
        cap.release()
        raise RuntimeError("Could not create output video.")

    print(f"Input: {width}x{height}, {fps:.2f} FPS, {total_frames} frames")
    print(f"Output: {output_width}x{output_height}")
    print(f"Backend: {engine.name}")

    processed_frames = 0
    started = time.perf_counter()

    try:
        while True:
            success, frame = cap.read()
            if not success:
                break

            enhanced = engine.enhance(frame)

            if enhanced.shape[1] != output_width or enhanced.shape[0] != output_height:
                raise RuntimeError(
                    f"Unexpected enhanced frame size: {enhanced.shape[1]}x{enhanced.shape[0]}"
                )

            writer.write(enhanced)
            processed_frames += 1
            print(f"Frame {processed_frames}/{total_frames}")
    finally:
        cap.release()
        writer.release()

    elapsed = time.perf_counter() - started

    return VideoResult(
        input_resolution=f"{width}x{height}",
        output_resolution=f"{output_width}x{output_height}",
        fps=fps,
        total_frames=total_frames,
        processed_frames=processed_frames,
        processing_seconds=elapsed,
    )
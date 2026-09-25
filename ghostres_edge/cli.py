from __future__ import annotations

import argparse
from pathlib import Path

from .engines.cuda_realesrgan import CudaRealESRGANEngine
from .video import enhance_video


def parse_arguments() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="GhostRes Edge — local AI video restoration"
    )

    parser.add_argument("--input", type=Path, default=Path("input.mp4"))
    parser.add_argument(
        "--output",
        type=Path,
        default=Path("output_edge_silent.mp4"),
        help="New output filename. Audio preservation comes in the next phase.",
    )
    parser.add_argument(
        "--scale",
        type=int,
        choices=[2, 4],
        default=4,
        help="Super-resolution scaling factor (2 or 4, default: 4).",
    )
    parser.add_argument(
        "--weights",
        type=Path,
        default=None,
        help="Path to model weights. Defaults to matching scale weights (weights/RealESRGAN_x{scale}plus.pth).",
    )
    parser.add_argument(
        "--realesrgan-root",
        type=Path,
        required=True,
        help="Path to your local Real-ESRGAN repository.",
    )
    parser.add_argument("--tile", type=int, default=128)
    parser.add_argument("--tile-pad", type=int, default=10)

    return parser.parse_args()


def main() -> None:
    args = parse_arguments()

    weights_path = args.weights
    if weights_path is None:
        weights_path = Path(f"weights/RealESRGAN_x{args.scale}plus.pth")

    print(f"Loading GhostRes Edge CUDA engine (scale: {args.scale}x)...")
    engine = CudaRealESRGANEngine(
        realesrgan_root=args.realesrgan_root,
        weights_path=weights_path,
        scale=args.scale,
        tile=args.tile,
        tile_pad=args.tile_pad,
        half=False,
    )

    result = enhance_video(
        input_path=args.input,
        output_path=args.output,
        engine=engine,
    )

    print("\nGhostRes Edge processing complete")
    print(f"Frames: {result.processed_frames}/{result.total_frames}")
    print(f"Output: {result.output_resolution}")
    print(f"Inference latency: {result.ms_per_frame:.2f} ms/frame")
    print(f"AI FPS: {result.ai_fps:.2f}")
    print(f"Total processing time: {result.processing_seconds:.2f} seconds")
    print(f"Total FPS: {result.total_fps:.2f}")
    print("Notice: this intermediate output does not yet preserve audio.")


if __name__ == "__main__":
    main()
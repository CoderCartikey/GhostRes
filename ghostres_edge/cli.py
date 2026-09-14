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
        "--weights",
        type=Path,
        default=Path("weights/RealESRGAN_x4plus.pth"),
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

    print("Loading GhostRes Edge CUDA engine...")
    engine = CudaRealESRGANEngine(
        realesrgan_root=args.realesrgan_root,
        weights_path=args.weights,
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
    print(f"Processing time: {result.processing_seconds:.2f} seconds")
    print(f"Output: {result.output_resolution}")
    print("Notice: this intermediate output does not yet preserve audio.")


if __name__ == "__main__":
    main()
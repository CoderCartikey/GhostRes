from __future__ import annotations

import argparse
from pathlib import Path
import sys
import torch


def parse_arguments() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Export RealESRGAN x4plus to a fixed-shape ONNX model for Snapdragon X Elite profiling."
    )
    parser.add_argument(
        "--realesrgan-root",
        type=Path,
        required=True,
        help="Path to your local Real-ESRGAN repository.",
    )
    parser.add_argument(
        "--weights",
        type=Path,
        default=Path("weights/RealESRGAN_x4plus.pth"),
        help="Path to model weights (default: weights/RealESRGAN_x4plus.pth).",
    )
    parser.add_argument(
        "--output",
        type=Path,
        default=Path("artifacts/realesrgan_x4_128.onnx"),
        help="Destination path for exported ONNX model (default: artifacts/realesrgan_x4_128.onnx).",
    )
    return parser.parse_args()


def setup_environment(realesrgan_root: Path) -> None:
    root = realesrgan_root.resolve()
    if not root.exists():
        raise FileNotFoundError(f"Real-ESRGAN folder not found: {root}")

    root_text = str(root)
    if root_text not in sys.path:
        sys.path.insert(0, root_text)

    if sys.platform == "win32":
        import os
        for candidate in [
            Path(sys.prefix) / "Library" / "bin",
            Path(sys.prefix).parent.parent / "Library" / "bin",
        ]:
            if candidate.is_dir():
                try:
                    os.add_dll_directory(str(candidate))
                except (OSError, AttributeError):
                    pass


def main() -> None:
    args = parse_arguments()
    setup_environment(args.realesrgan_root)

    from basicsr.archs.rrdbnet_arch import RRDBNet

    weights_path = args.weights.resolve()
    output_path = args.output.resolve()

    if not weights_path.is_file():
        raise FileNotFoundError(f"Weights file not found: {weights_path}")

    output_path.parent.mkdir(parents=True, exist_ok=True)

    print(f"Loading weights from: {weights_path}")
    checkpoint = torch.load(str(weights_path), map_location="cpu")

    if isinstance(checkpoint, dict):
        if "params_ema" in checkpoint:
            state_dict = checkpoint["params_ema"]
        elif "params" in checkpoint:
            state_dict = checkpoint["params"]
        else:
            state_dict = checkpoint
    else:
        state_dict = checkpoint

    model = RRDBNet(
        num_in_ch=3,
        num_out_ch=3,
        num_feat=64,
        num_block=23,
        num_grow_ch=32,
        scale=4,
    )
    model.load_state_dict(state_dict, strict=True)
    model.to(torch.float32).cpu().eval()

    dummy_input = torch.zeros(1, 3, 128, 128, dtype=torch.float32, device="cpu")

    print("Exporting model to ONNX (opset 11)...")
    torch.onnx.export(
        model,
        dummy_input,
        str(output_path),
        export_params=True,
        opset_version=11,
        do_constant_folding=True,
        input_names=["image"],
        output_names=["enhanced"],
    )

    import onnx
    print("Validating exported ONNX model...")
    onnx_model = onnx.load(str(output_path))
    onnx.checker.check_model(onnx_model)

    expected_output_shape = (1, 3, 128 * 4, 128 * 4)
    print("\nONNX export completed successfully!")
    print(f"Input shape:  {list(dummy_input.shape)}")
    print(f"Output shape: {list(expected_output_shape)}")
    print(f"Output path:  {output_path}")


if __name__ == "__main__":
    main()

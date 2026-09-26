from __future__ import annotations

import argparse
from pathlib import Path
import sys


def parse_arguments() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Compile and profile RealESRGAN x4 ONNX model on Qualcomm AI Hub for Snapdragon X Elite."
    )
    parser.add_argument(
        "--model",
        type=Path,
        default=Path("artifacts/realesrgan_x4_128.onnx"),
        help="Path to the ONNX model file (default: artifacts/realesrgan_x4_128.onnx).",
    )
    parser.add_argument(
        "--device",
        type=str,
        default="Snapdragon X Elite CRD",
        help="Target physical device name on Qualcomm AI Hub (default: 'Snapdragon X Elite CRD').",
    )
    return parser.parse_args()


def main() -> None:
    args = parse_arguments()

    model_path = args.model.resolve()
    if not model_path.is_file():
        print(f"Error: Model file not found: {model_path}", file=sys.stderr)
        sys.exit(1)

    import qai_hub as hub

    device = hub.Device(args.device)

    print("Submitting compile job to Qualcomm AI Hub...")
    print(f"  Model:  {model_path}")
    print(f"  Device: {device.name}")
    print("  Runtime: QNN DLC (Compute Unit: NPU)")

    compile_job = hub.submit_compile_job(
        model=str(model_path),
        device=device,
        options="--target_runtime qnn_dlc --compute_unit npu",
        name="GhostResEdge_RealESRGAN_x4_XElite_QNN",
    )

    print(f"\nCompile job submitted:")
    print(f"  URL: {compile_job.url}")

    print("\nWaiting for compile job completion...")
    compile_status = compile_job.wait()
    print(f"Compile job status: {compile_status}")

    try:
        target_model = compile_job.get_target_model()
        if target_model is None:
            raise RuntimeError("Compile job returned no target model.")
    except Exception as e:
        print(f"\nError obtaining target model: {e}", file=sys.stderr)
        print(f"  URL: {compile_job.url}", file=sys.stderr)
        sys.exit(1)

    print("\nSubmitting profile job to Qualcomm AI Hub...")
    print(f"  Device: {device.name}")
    print("  Compute Unit: NPU")

    profile_job = hub.submit_profile_job(
        model=target_model,
        device=device,
        options="--compute_unit npu",
        name="GhostResEdge_RealESRGAN_x4_XElite_Profile",
    )

    print(f"\nProfile job submitted:")
    print(f"  URL: {profile_job.url}")

    print("\nWaiting for profile job completion...")
    profile_status = profile_job.wait()
    print(f"Profile job status: {profile_status}")

    if "SUCCESS" not in str(profile_status).upper():
        print("\nProfile job failed.", file=sys.stderr)
        print(f"  URL: {profile_job.url}", file=sys.stderr)
        sys.exit(1)

    print("\nSnapdragon X Elite NPU profiling completed successfully!")
    print(f"  Compile Job URL: {compile_job.url}")
    print(f"  Profile Job URL: {profile_job.url}")


if __name__ == "__main__":
    main()

# GhostRes Edge

> Local desktop AI video restoration with Fast 2x and Quality 4x modes, measurable inference telemetry, and a validated path to Snapdragon QNN deployment.

GhostRes Edge restores low-resolution video using Real-ESRGAN neural super-resolution. Instead of simply stretching pixels, it predicts plausible higher-frequency detail to improve edges, textures, and visual clarity.

The working desktop application runs locally on an NVIDIA CUDA development machine. Its exported Real-ESRGAN x4 ONNX tile has also been compiled and profiled on a Qualcomm Snapdragon X Elite CRD through Qualcomm AI Hub using the QNN NPU.

## Project Status

| Area | Status |
|---|---|
| Desktop application | Working CustomTkinter application |
| Local AI restoration | Working on NVIDIA CUDA |
| Fast mode | Real-ESRGAN x2plus |
| Quality mode | Real-ESRGAN x4plus |
| Video telemetry | Inference latency, AI FPS, total FPS, resolution, and frame count |
| ONNX export | Validated x4 fixed-tile ONNX model |
| Snapdragon X Elite validation | Profiled through Qualcomm AI Hub with QNN NPU |
| Native Snapdragon desktop engine | Planned next step |

> Important: the current desktop application is CUDA based. Snapdragon AI Hub results validate the exported model on Snapdragon X Elite hardware; they are not presented as a completed end-to-end native Snapdragon desktop application.

## Why GhostRes Edge

Low-resolution and compressed video often becomes blurry when enlarged with traditional interpolation. GhostRes Edge uses neural super-resolution to reconstruct plausible visual detail locally instead of sending footage to a cloud service.

The application makes the speed-versus-quality tradeoff explicit:

- **Fast 2x**: lower output resolution and faster restoration.
- **Quality 4x**: higher output resolution with significantly higher compute cost.
- **Local workflow**: source footage stays on the development machine during CUDA processing.
- **Measured telemetry**: model-only inference latency is reported separately from total processing time.
- **Portable architecture**: the UI and video pipeline are separated from the inference backend, allowing CUDA and future QNN engines to share one workflow.

## Desktop Application

Run the application:

```bash
conda activate webcam-ai
python app.py
```

The desktop workflow is:

1. Select a video.
2. Choose **Fast 2x** or **Quality 4x**.
3. Start restoration.
4. Review processing metrics.
5. Open the completed output video or output folder.

The current implementation uses CustomTkinter for the desktop UI, OpenCV for video I/O, and a modular enhancement backend for model inference.

## Architecture

```text
Video input
    |
    v
CustomTkinter desktop application
    |
    v
Video pipeline
- reads frames
- writes enhanced frames
- validates output dimensions
- records processing telemetry
    |
    v
EnhancementEngine interface
    |
    +--> CUDA Real-ESRGAN engine
    |    - x2plus Fast mode
    |    - x4plus Quality mode
    |    - tiled CUDA inference
    |
    +--> Future ONNX Runtime QNN engine
         - Snapdragon Windows deployment path
```

The application depends on an `EnhancementEngine` interface. The UI and video pipeline do not need to know whether enhancement is performed by the current CUDA engine or a future Snapdragon QNN engine.

## Measured Results

### Local desktop application result

Measured using Fast 2x mode on the RTX 3060 development laptop:

| Input | Output | Frames | Total time | AI latency | AI FPS |
|---|---:|---:|---:|---:|---:|
| 864x480 video | 1728x960 | 95 | 165.46 seconds | 1720 ms per frame | 0.58 |

This is an end-to-end local video restoration result, including frame processing and video output.

### Snapdragon X Elite model profile

The Real-ESRGAN x4 model was exported as a fixed-shape ONNX tile model and profiled through Qualcomm AI Hub.

| Target | Runtime | Input tensor | Result |
|---|---|---|---|
| Snapdragon X Elite CRD running Windows 11 | QNN NPU | `float32 [1, 3, 128, 128]` | 65.4 ms inference latency, 0.2 MB peak memory |

The ONNX output tensor is:

```text
enhanced: float32 [1, 3, 512, 512]
```

> These results measure different workloads. The RTX result is full-video processing, while the Snapdragon result is one fixed-size model tile. They should not be compared as equivalent FPS benchmarks.

## Snapdragon Readiness

GhostRes Edge creates a practical path from an existing local CUDA prototype to Snapdragon deployment:

1. Real-ESRGAN x4plus model loaded from verified weights.
2. Fixed-shape 128x128 input tile exported to ONNX.
3. ONNX model validated using `onnx.checker`.
4. Model compiled and profiled through Qualcomm AI Hub.
5. Profile executed on Snapdragon X Elite CRD with QNN NPU.
6. Next step: implement an `OnnxQnnEngine` that uses the compiled model in the same desktop workflow.

## Repository Structure

```text
GhostRes/
|
├── app.py
├── ghostres_edge/
│   ├── cli.py
│   ├── video.py
│   └── engines/
│       ├── base.py
│       └── cuda_realesrgan.py
|
├── scripts/
│   ├── export_realesrgan_x4_onnx.py
│   └── profile_snapdragon_x_elite.py
|
├── weights/
│   ├── RealESRGAN_x2plus.pth
│   └── RealESRGAN_x4plus.pth
|
├── artifacts/
│   └── realesrgan_x4_128.onnx
|
├── smoke_input.mp4
└── README.md
```

## Setup

### Requirements

- Windows 10 or Windows 11
- NVIDIA GPU with CUDA support for the current desktop engine
- Python 3.10
- Conda or Anaconda
- NVIDIA CUDA drivers
- A local clone of the Real-ESRGAN repository

### Clone the project

```bash
git clone https://github.com/CoderCartikey/GhostRes.git
cd GhostRes
git checkout ghostres-edge
```

### Create the environment

```bash
conda create -n webcam-ai python=3.10
conda activate webcam-ai
```

### Install PyTorch and dependencies

```bash
pip install torch==2.2.0 torchvision==0.17.0 --index-url https://download.pytorch.org/whl/cu121
pip install "numpy<2"
pip install basicsr facexlib gfpgan realesrgan
pip install opencv-python customtkinter onnx
```

### Clone Real-ESRGAN

```bash
git clone https://github.com/xinntao/Real-ESRGAN.git C:\Users\YOUR_USERNAME\Real-ESRGAN
```

Ensure the Real-ESRGAN path used by GhostRes Edge points to your local clone.

## Command Line Usage

### Quality 4x

```bash
conda activate webcam-ai

python -m ghostres_edge.cli ^
  --input input.mp4 ^
  --output restored_4x.mp4 ^
  --weights weights\RealESRGAN_x4plus.pth ^
  --realesrgan-root "C:\Users\YOUR_USERNAME\Real-ESRGAN" ^
  --scale 4
```

### Fast 2x

```bash
conda activate webcam-ai

python -m ghostres_edge.cli ^
  --input input.mp4 ^
  --output restored_2x.mp4 ^
  --weights weights\RealESRGAN_x2plus.pth ^
  --realesrgan-root "C:\Users\YOUR_USERNAME\Real-ESRGAN" ^
  --scale 2
```

Use a short clip first. Real-ESRGAN runs neural inference on every frame, so processing time grows quickly with resolution, frame count, and selected scale.

## Development Environment

| Component | Technology |
|---|---|
| Desktop UI | CustomTkinter |
| Video processing | OpenCV |
| Super-resolution models | Real-ESRGAN x2plus and x4plus |
| Deep-learning framework | PyTorch 2.2.0 |
| Desktop acceleration | NVIDIA CUDA |
| Model portability | ONNX |
| Snapdragon validation | Qualcomm AI Hub and QNN NPU |
| Development hardware | RTX 3060 laptop GPU |

## Limitations

- The current local desktop engine requires an NVIDIA GPU with CUDA.
- The current intermediate video output does not yet preserve source audio.
- Super-resolution generates plausible reconstructed detail; it does not prove that missing detail existed in the original footage.
- The Snapdragon result is a fixed-tile model profile, not an end-to-end video benchmark.
- Full Quality 4x restoration is compute-intensive.

## Roadmap

- [ ] Implement an ONNX Runtime QNN or QNN-backed `EnhancementEngine`.
- [ ] Run full-video benchmarks on a physical Snapdragon PC.
- [ ] Preserve source audio during output creation.
- [ ] Add queue or batch processing.
- [ ] Package the desktop application with guided model and runtime setup.
- [ ] Add configurable tile size and memory-aware performance modes.

## Project Lineage

GhostRes Edge is a significant modular evolution of the original GhostRes project. It adds:

- a usable desktop application,
- switchable 2x and 4x enhancement,
- a reusable inference-engine boundary,
- measured processing telemetry,
- ONNX export,
- Snapdragon X Elite QNN profiling evidence.

## Built By

**Kartikey Bhardwaj**  
B.Tech CSE, DIT University

[GitHub](https://github.com/CoderCartikey) · [LinkedIn](https://www.linkedin.com/in/kartikey-bhardwaj-in)

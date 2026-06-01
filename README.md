# GhostRes 👻

Open source · No API · No internet · No subscription · Your videos stay on your PC 🔒

Takes a low-resolution video (480p/720p) and enhances it to 2x/4x resolution using 
Real-ESRGAN neural network running locally on your NVIDIA GPU.

## Requirements

**Hardware:**
- NVIDIA GPU (CUDA compatible) *(no GPU = 💀)*

**Software:**
- Python 3.10
- Anaconda
- NVIDIA CUDA drivers

## Setup

```bash
# 1. Clone this repo
git clone https://github.com/CoderCartikey/GhostRes.git
cd GhostRes

# 2. Create conda environment
conda create -n webcam-ai python=3.10
conda activate webcam-ai

# 3. Install PyTorch with CUDA
pip install torch==2.2.0 torchvision==0.17.0 --index-url https://download.pytorch.org/whl/cu121

# 4. Install dependencies
pip install "numpy<2"
pip install basicsr facexlib gfpgan realesrgan

# 5. Clone Real-ESRGAN separately
git clone https://github.com/xinntao/Real-ESRGAN.git C:\Users\YOUR_USERNAME\Real-ESRGAN

# 6. Download model weights
python download_model.py
```

> ⚠️ numpy must be < 2 — if it upgrades automatically, run `pip install "numpy<2" --force-reinstall --no-deps`

## Usage

1. Rename your video to `input.mp4` and put it in project folder *(yes you have to rename it dawg)*
2. Run:
```bash
python video_enhance.py
```
3. Enhanced output saved as `output.mp4` *(may take time depending on your GPU — go touch grass)*

## Demo

![GhostResDemo](GhostResDEMO.mp4)


## How it works

When you upscale a video the normal way, pixels just get stretched — 
edges become blurry and details get lost.

GhostRes uses Real-ESRGAN, a neural network trained on thousands of 
images, to **predict** what those missing pixels should look like — 
not just stretch them. It reconstructs edges, textures, and fine 
details that weren't visible in the original footage.

Result: your 480p video doesn't just get bigger — it gets sharper.

## Results
![Results](GhostRes1.png)

## Built by
Kartikey Bhardwaj · B.Tech CSE · DIT University · 2nd Year *(I guess passed.)*

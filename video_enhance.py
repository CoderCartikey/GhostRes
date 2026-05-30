import cv2
import sys
sys.path.insert(0, r'C:\Users\ASUS\Real-ESRGAN')
from realesrgan import RealESRGANer
from basicsr.archs.rrdbnet_arch import RRDBNet

model = RRDBNet(num_in_ch=3, num_out_ch=3, num_feat=64, num_block=23, num_grow_ch=32, scale=4)

upsampler = RealESRGANer(
    scale=4,
    model_path="weights/RealESRGAN_x4plus.pth",
    model=model,
    tile=128,
    tile_pad=10,
    pre_pad=0,
    half=False
)
print("Model loaded!")

cap = cv2.VideoCapture("input.mp4")
total_frames = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
fps = cap.get(cv2.CAP_PROP_FPS)
print(f"Total frames: {total_frames}, FPS: {fps}")

width = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
height = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))

out = cv2.VideoWriter(
    "output.mp4",
    cv2.VideoWriter_fourcc(*'XVID'),
    fps,
    (width * 2, height * 2)
)

frame_count = 0
while True:
    ret, frame = cap.read()
    if not ret:
        break

    enhanced, _ = upsampler.enhance(frame, outscale=2)
    out.write(enhanced)

    frame_count += 1
    print(f"Frame {frame_count}/{total_frames}")

cap.release()
out.release()
cv2.destroyAllWindows()
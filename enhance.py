import cv2
import torch
import numpy as np
import threading
import queue
import time
import sys
sys.path.insert(0, r'C:\Users\ASUS\Real-ESRGAN')
from realesrgan import RealESRGANer
from basicsr.archs.rrdbnet_arch import RRDBNet

model = RRDBNet(num_in_ch=3, num_out_ch=3, num_feat=64, num_block=23, num_grow_ch=32, scale=2)

upsampler = RealESRGANer(
    scale=2,
    model_path="weights/RealESRGAN_x2plus.pth",
    model=model,
    tile=64,
    tile_pad=10,
    pre_pad=0,
    half=True
)
print("Model loaded!")

frame_queue = queue.Queue(maxsize=1)
result_queue = queue.Queue(maxsize=1)

def capture_thread():
    cap = cv2.VideoCapture(0)
    while True:
        ret, frame = cap.read()
        if not ret:
            break
        if frame_queue.full():
            frame_queue.get()
        frame_queue.put(frame)
    cap.release()

def enhance_thread():
    fps_counter = 0
    start_time = time.time()
    frame_skip = 0
    while True:
        frame = frame_queue.get()
        frame_skip += 1

        if frame_skip % 5 ==0:
            output, _ = upsampler.enhance(frame, outscale=2)

        else:
            output = frame

        if result_queue.full():
            result_queue.get()
        result_queue.put((frame, output))
        fps_counter += 1
        elapsed = time.time() - start_time
        print(f"FPS: {fps_counter/elapsed:.1f}")

t1 = threading.Thread(target=capture_thread, daemon=True)
t2 = threading.Thread(target=enhance_thread, daemon=True)
t1.start()
t2.start()

while True:
    if not result_queue.empty():
        original, enhanced = result_queue.get()
        cv2.imshow('original', original)
        cv2.imshow('Enhanced', enhanced)
    if cv2.waitKey(1) == ord('q'):
        break

cv2.destroyAllWindows()
import requests
import os

url =  "https://github.com/xinntao/Real-ESRGAN/releases/download/v0.2.1/RealESRGAN_x2plus.pth"
save_path = "weights/RealESRGAN_x2plus.pth"

os.makedirs("weights", exist_ok=True)

print("Downloading model...")
response = requests.get(url, stream=True)
with open(save_path, "wb") as f:
    for chunk in response.iter_content(chunk_size=8192):
        f.write(chunk)

        print("Done! Model saved to", save_path)
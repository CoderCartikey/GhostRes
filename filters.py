import cv2

cap = cv2.VideoCapture(0)

while True:
    ret, frame = cap.read()

    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

    blurred = cv2.GaussianBlur(gray, (3, 3), 0)

    sharp = cv2.addWeighted(gray, 1.5, blurred, -0.5,0)

    bright = cv2.convertScaleAbs(gray, alpha=1.5, beta=30)

    cv2.imshow("Gray", gray)
    cv2.imshow("Blurred", blurred)
    cv2.imshow("Sharp", sharp)
    cv2.imshow("Bright", bright)

    if cv2.waitKey(1) == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()
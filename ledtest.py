import RPi.GPIO as GPIO
import time
GPIO.cleanup()
GPIO.setmode(GPIO.BOARD)
GPIO.setup(31, GPIO.OUT)
while True:
    GPIO.output(31, GPIO.HIGH)
    time.sleep(0.25)
    GPIO.output(31, GPIO.LOW)
    time.sleep(0.25)

import RPi.GPIO as GPIO
GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)
GPIO.setup(25, GPIO.OUT)
def blinkylights_on():
    GPIO.output(25, GPIO.HIGH)
def blinkylights_off():
    GPIO.output(25, GPIO.LOW)

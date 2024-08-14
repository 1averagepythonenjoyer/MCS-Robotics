import RPi.GPIO as GPIO
GPIO.setwarnings(False)
GPIO.setmode(GPIO.BOARD)
GPIO.setup(22, GPIO.OUT)
def blinkylights_on():
    GPIO.output(22, GPIO.HIGH)
def blinkylights_off():
    GPIO.output(22, GPIO.LOW)


import RPi.GPIO as GPIO


GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)

input1 = 18
GPIO.setup(input1, GPIO.OUT)
GPIO.output(input1, GPIO.HIGH)

input2 = 19
GPIO.setup(input2, GPIO.OUT)
GPIO.output(input2, GPIO.LOW)

enable1 = 12
GPIO.setup(enable1, GPIO.OUT)
GPIO.ouput(enable1, GPIO.HIGH)

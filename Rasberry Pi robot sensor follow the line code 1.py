pip install RPi.GPIO
import RPi.GPIO as GPIO
import time



GPIO.setmode(GPIO.BOARD)
left_motor = [,]  #customise
right_motor = [,]  #customise
GPIO.setup(left_motor, GPIO.OUT)
GPIO.setup(right_motor, GPIO.OUT)
left_sensors = [,]  #customise
right_sensors = [,] #customise

GPIO.setup(left_sensors, GPIO.IN)
GPIO.setup(right_sensors, GPIO.IN)
def forward():
    GPIO.output(left_motor[0], GPIO.HIGH)
    GPIO.output(left_motor[1], GPIO.LOW)
    GPIO.output(right_motor[0], GPIO.HIGH)
    GPIO.output(right_motor[1], GPIO.LOW)
def left():
    GPIO.output(left_motor[0], GPIO.LOW)
    GPIO.output(left_motor[1], GPIO.HIGH)
    GPIO.output(right_motor[0], GPIO.HIGH)
    GPIO.output(right_motor[1], GPIO.LOW)
def right():
    GPIO.output(left_motor[0], GPIO.HIGH)
    GPIO.output(left_motor[1], GPIO.LOW)
    GPIO.output(right_motor[0], GPIO.LOW)
    GPIO.output(right_motor[1], GPIO.HIGH)
try:
    while True:
        left_sensor_values = [GPIO.input(pin) for pin in left_sensors]
        right_sensor_values = [GPIO.input(pin) for pin in right_sensors]
        if all(value == GPIO.LOW for value in left_sensor_values) and all(value == GPIO.LOW for value in right_sensor_values):
            forward()
        elif any(value == GPIO.HIGH for value in left_sensor_values) and all(value == GPIO.LOW for value in right_sensor_values):
            right()
        elif all(value == GPIO.LOW for value in left_sensor_values) and any(value == GPIO.HIGH for value in right_sensor_values):
            left()
        else:
            #This coould mean both sensors on or off the line so I will leave this until disccussion
            pass
except KeyboardInterrupt:
    GPIO.cleanup()

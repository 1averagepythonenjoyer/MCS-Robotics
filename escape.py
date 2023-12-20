import RPi.GPIO as GPIO 
from gpiozero import Motor 
import time 

left_motor = Motor(forward = ?, backward = ?) #customise 
right_motor = Motor(forward = ?, backward = ?) #customise 
left_sensor_pin = #put number for GPIO pin 
right_sensor_pin = #same as line 10 
GPIO.setmode(GPIO.BCM) 
GPIO.setup(left_sensor_pin, GPIO.IN) 
GPIO.setup(right_sensor_pin, GPIO.IN) 

def obstacles_left(): 
    return GPIO.input(left_sensor_pin) == GPIO.HIGH 
def obstacles_right(): 
    return GPIO.input(right_sensor_pin) == GPIO.HIGH 

while True: 
    if not obstacle_left() and not obstacle_right(): 
        left_motor.forward() 
        right_motor.forward() 
    elif not obstacle_left() and obstacle_right(): 
        left_motor.backward() 
        right_motor.forward() 
    elif obstacle_left() and not obstacle_right(): 
        left_motor.forward() 
        right_motor.backward() 
    else: 
        left_motor.forward() 
        right_motor.forward() 
    time.sleep(0.1) #adjust! 
left_motor.stop() 
right_motor.stop() 
GPIO.cleanup() 

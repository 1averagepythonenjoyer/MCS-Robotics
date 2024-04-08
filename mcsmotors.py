from time import sleep
import RPi.GPIO as GPIO # remember to install
from approxeng.input.selectbinder import ControllerResource # remember to install


GPIO.setwarnings(False)
GPIO.setmode(GPIO.BOARD)

PWM_ENA = 13  
GPIO.setup(PWM_ENA, GPIO.OUT)
PWMa = GPIO.PWM(PWM_ENA, 100)
PWMa.start(0)

PWM_ENB = 15
GPIO.setup(PWM_ENB, GPIO.OUT)
PWMb= GPIO.PWM(PWM_ENB, 100)
PWMb.start(0)

#h bridge logic 
#currently set to forward motion
IN1= 19      #INPUT1 and INPUT2 logic pins on L298 control direction of left motor i believe
GPIO.setup(IN1, GPIO.OUT) #double check which way round it is
GPIO.output(IN1, GPIO.HIGH)
IN2 = 21
GPIO.setup(IN2, GPIO.OUT)
GPIO.output(IN2, GPIO.LOW)
IN3 = 16      #INPUT3 and INPUT4 logic pins control right motor
GPIO.setup(IN3, GPIO.OUT)
GPIO.output(IN3, GPIO.HIGH)
IN4 = 18
GPIO.setup(IN4, GPIO.OUT)
GPIO.output(IN4, GPIO.LOW)

RMcorrection = 1.0 #to offset drift of motors (1 motor spinning faster than the other: left motor spins faster than the right I think)
LMcorrection = 0.8

def map_range(a, input_min, input_max, output_min, output_max): #nicky's function. Not entirely sure what it does. 
    return (a - input_min) * (output_max - output_min) // (input_max - input_min) + output_min #a is placeholder value

def setmotor(left_power, right_power):
    #check h bridge
    if left_power < 0:
        GPIO.output(IN1, GPIO.LOW)
        GPIO.output(IN2, GPIO.HIGH)
    if left_power >= 0:
        GPIO.output(IN1, GPIO.HIGH)
        GPIO.output(IN2, GPIO.LOW)
    if right_power < 0:
        GPIO.output(IN1, GPIO.LOW)
        GPIO.output(IN2, GPIO.HIGH)
    if right_power >= 0:
        GPIO.output(IN1, GPIO.HIGH)
        GPIO.output(IN2, GPIO.LOW)
    global lmotor 
    global rmotor
    lmotor = map_range(left_power,0,100,0,100) #values to be sent to motors
    rmotor = map_range(right_power,0,100,0,100) 
    #send values to motor controller 
    PWMa.ChangeDutyCycle(lmotor * LMcorrection)
    PWMb.ChangeDutyCycle(rmotor * RMcorrection)
    
    
    

    
    


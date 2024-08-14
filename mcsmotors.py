
import RPi.GPIO as GPIO # remember to install
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
LMcorrection = 0.95
def yawthrottle(yaw, throttle, speedfactor):   #yaw and throttle need to be floats from -1.0 to 1.0
    left = (throttle + yaw)
    right = (throttle - yaw)
    scale = float(100) / max(1, abs(left), abs(right))
    setmotor(right*scale, left*scale, speedfactor)


def setmotor(right_power, left_power, speedfactor):
    print("Right power:", right_power)

    #check h bridge
    if right_power < 0:
        GPIO.output(IN1, GPIO.LOW)
        GPIO.output(IN2, GPIO.HIGH)
    elif right_power >= 0:
        GPIO.output(IN1, GPIO.HIGH)
        GPIO.output(IN2, GPIO.LOW)
    if left_power < 0:
        GPIO.output(IN3, GPIO.LOW)
        GPIO.output(IN4, GPIO.HIGH)
    elif left_power >= 0:
        GPIO.output(IN3, GPIO.HIGH)
        GPIO.output(IN4, GPIO.LOW)
    #rmotor = map_range(right_power,0,100,0,100) 
    #lmotor = map_range(left_power,0,100,0,100)
     #values to be sent to motors
    #send values to motor controller
    #print("Right power:", right_power) 
    #print("Left power:", left_power)
    #right_power = abs(right_power)
    if -100 <= right_power <= 100:
        PWMa.ChangeDutyCycle(int(abs(right_power) * RMcorrection*speedfactor)) #abs() always returns positive so changedutycycle doesnt give an error. 
    if -100 <= left_power <= 100:
        PWMb.ChangeDutyCycle(int(abs(left_power) * LMcorrection*speedfactor))
    print("Right Duty:", abs(right_power) * RMcorrection)
    print("Left Duty: ", abs(left_power) * LMcorrection)
    
    

    
    
    

    
    

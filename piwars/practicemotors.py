import RPi.GPIO as GPIO
GPIO.setmode(GPIO.BOARD) 
GPIO.setwarnings(False)

LmotorF = 22
LmotorR = 12
RmotorF = 18
RmotorR = 16

# Use values bettwen 0 and 1, lowering value slows down the motion
LmotorF_correction = 1.0
LmotorR_correction = 1.0
RmotorF_correction = 1.0
RmotorR_correction = 1.0

GPIO.setup(LmotorF, GPIO.OUT)
GPIO.setup(LmotorR, GPIO.OUT)
GPIO.setup(RmotorF, GPIO.OUT)
GPIO.setup(RmotorR, GPIO.OUT)
# setting initial values
LFmotor = GPIO.PWM(LmotorF, 150)
LRmotor = GPIO.PWM(LmotorR, 150)
RFmotor = GPIO.PWM(RmotorF, 150)
RRmotor = GPIO.PWM(RmotorR, 150)

LFmotor.start(0)
LRmotor.start(0)
RFmotor.start(0)
RRmotor.start(0)


def set(power_left, power_right):
    print('power left: ', power_left, '   power right: ', power_right)


    if  10.0 <= power_left <= 100.0: # Left motor forwad motion
        print('setting LF', power_left * LmotorF_correction)
        LFmotor.ChangeDutyCycle(power_left * LmotorF_correction)
        LRmotor.ChangeDutyCycle(0.0)

    elif  -100.0 <= power_left <= -10.0: # Left motor reverse motion
        print('setting LR', -1 * power_left * LmotorR_correction)
        LRmotor.ChangeDutyCycle(-1 * power_left * LmotorR_correction)
        LFmotor.ChangeDutyCycle(0.0)
    else:
        LFmotor.ChangeDutyCycle(0.0)
        LRmotor.ChangeDutyCycle(0.0)

    if  10.0 <= power_right <= 100.0: # Right motor forwad motion
        print('setting RF', power_right * RmotorF_correction)
        RFmotor.ChangeDutyCycle(power_right * RmotorF_correction)
        RRmotor.ChangeDutyCycle(0.0)

    elif  -100.0 <= power_right <= -10.0: # Right motor reverse motion
        print('setting RR', -1 * power_right * RmotorR_correction)
        RRmotor.ChangeDutyCycle(-1 * power_right * RmotorR_correction)
        RFmotor.ChangeDutyCycle(0.0)
 
    else:
        RFmotor.ChangeDutyCycle(0.0)
        RRmotor.ChangeDutyCycle(0.0)

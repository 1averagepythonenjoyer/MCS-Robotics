import time
import RPi.GPIO as GPIO 
from approxeng.input.selectbinder import ControllerResource 

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BOARD)

sol_ctrl = 8  #solenoid control pin. Pin 8, GPIO 8 (BOARD, BCM) ALways use BOARD

GPIO.setup(sol_ctrl, GPIO.OUT)


def zombie():
    while joystick.connected:
            joystick.check_presses()
            # Check for a button press
            if joystick.presses.cross:
                print('Fire request given waiting 1second...') #waiting is just for now: REMEMBER TO REMOVE BEFORE COMPETITION: DEAD TIME
                time.sleep(1)
                GPIO.output(sol_ctrl, GPIO.HIGH)
                time.sleep(0.1) #hopefully this is enough. may need to modify. 
                GPIO.output(sol_ctrl, GPIO.LOW)


zombie()          
            
            

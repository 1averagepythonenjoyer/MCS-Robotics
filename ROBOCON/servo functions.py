from gpiozero import AngularServo
from time import sleep


clamp_pin = #pin for clamp servo (default is BCM)
arm_pin = #pin for arm servo (default is BCM)
lair_a = #angle needed to reach lair (for arm)
pick_a = #angle needed to reach and pick up box (for arm)
secure_a = #angle needed for clamp servo to secure the box in its grip
stor_a = #angle of arm so it can release and drop into box

servo_a = AngularServo(clam_pin, min_angle = 0, max_angle = 180, min_pulse_width = 0.0005, max_pulse_width = 0.0025) #clamp
#Without hardware PWM there will be jitter for our design it does not matter because the jitter will not affect angle as the rubber bands cancel it
#I'm not too sure whether the min and max pulse width value is correct but these values make the servo move whereas the deafult value just does not work
servo_b = AngularServo(arm_pin, min_angle = 0, max_angle = 180, min_pulse_width = 0.0005, max_pulse_width = 0.0025) #arm

def grabbox():
    servo_b.angle = pick_a
    sleep(1) #Sleep here so clamp can get into position first
    servo_a.angle = secure_a
    sleep(1)
    
def releasebox():
    servo_a.angle = 0
    sleep(2)
    
def storebox():
    servo_b.angle = stor_a
    sleep(1) #Sleep here so clamp can get into position first
    servo_a.angle = 0
    sleep(1)
    
def box_to_lair():
    servo_b = lair_a
    sleep(1)
    servo_a = 0
    sleep(2)
    
def resetarmpos():
    servo_b.angle = pick_a
    servo_a.angle = 30 #some random value
    

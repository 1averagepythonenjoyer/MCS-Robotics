import smbus2
import robot

from time import sleep

bus = smbus2.SMBus(1)
r = robot.Robot() #dont need in main

r.servos[0].mode = robot.PWM_SERVO 
r.servos[1].mode = robot.PWM_SERVO 

def set_arm_servo_to_angle(angle):
#angle range = 1700 , 4600, 7500
#mid value 70
    value = 1700 + int(angle/180*5800)
    bus.write_byte_data(0x08, 1, value >> 8)
    bus.write_byte_data(0x08, 2, value & 0xff)

def set_claw_servo_to_angle(angle):
#angle range = 1700 , 4600, 7500
#mid value 70 
    value = 1700 + int(angle/180*5800)
    bus.write_byte_data(0x08, 3, value >> 8)
    bus.write_byte_data(0x08, 4, value & 0xff)



set_arm_servo_to_angle(3)
sleep(2)
set_claw_servo_to_angle(10)
sleep(2)
set_claw_servo_to_angle(160)
sleep(2)
set_arm_servo_to_angle(170)
sleep(2)
set_claw_servo_to_angle(10)
sleep(2)
set_arm_servo_to_angle(0)
# set_arm_servo_to_angle(180)
# sleep(2)
# set_claw_servo_to_angle(0)

from gpiozero import Motor, DistanceSensor #Unlike Rpi.GPIO, gpiozero always uses BCM. This cannot be configured unfortunately so just keep in mind when assigning GPIO pins
import time


full_speed_ahead = 1
small_turn = 0.9
medium turn = 0.8
big_turn = 0.5
emergency_turn = 0.3

left_motor = Motor(forward = ,backward = )
right_motor = Motor(forward = ,backward = )

front_sensor = DistanceSensor(echo =, trigger =)
left_sensor = DistanceSensor(echo =, trigger =)
right_sensor = DistanceSensor(echo =, trigger =)
back_sensor = DistanceSensor(echo =, trigger =)

def forward_fast():
    left_motor.forward(full_speed_ahead)
    right_motor.forward(full_speed_ahead)
    
def forward_medium():
    left_motor.forward(small_turn)
    right_motor.forward(small_turn)

def forward_slow():
    left_motor.forward(medium_turn)
    right_motor.forward(medium_turn)
    
def left_small():
    left_motor.forward(small_turn)
    right_motor.forward(full_speed_ahead)
    
def right_small():
    left_motor.forward(full_speed_ahead)
    right_motor.forward(small_turn)
    
def left_medium():
    left_motor.forward(medium_turn)
    right_motor.forward(full_speed_ahead)
    
def right_medium():
    left_motor.forward(full_speed_ahead)
    right_motor.forward(medium_turn)
    
def left_big():
    left_motor.forward(big_turn)
    right_motor.forward(full_speed_ahead)
    
def right_big():
    left_motor.forward(full_speed_ahead)
    right_motor.forward(big_turn)
    
def backward_fast():
    left_motor.backward(full_speed_ahead)
    right_motor.backward(full_speed_ahead)

def backward_medium():
    left_motor.backward(small_turn)
    right_motor.backward(small_turn)
    
def backward_slow():
    left_motor.backward(medium_turn)
    right_motor.backward(medium_turn)
    
def backward_left_small():
    left_motor.forward(small_turn)
    right_motor.forward(full_speed_ahead)
    
def backward_right_small():
    left_motor.forward(full_speed_ahead)
    right_motor.forward(small_turn)
    
def backward_left_medium():
    left_motor.forward(medium_turn)
    right_motor.forward(full_speed_ahead)
    
def backward_right_medium():
    left_motor.forward(full_speed_ahead)
    right_motor.forward(medium_turn)
    
def backward_left_big():
    left_motor.forward(big_turn)
    right_motor.forward(full_speed_ahead)
    
def backward_right_big():
    left_motor.forward(full_speed_ahead)
    right_motor.forward(big_turn)
    
def backward_left_emergency():
    left_motor.forward(emergency_turn)
    right_motor.forward(full_speed_ahead)
    
def backward_right_emergency():
    left_motor.forward(full_speed_ahead)
    right_motor.forward(emergency_turn)
    
def stop():
    left_motor.stop()
    right_motor.stop()
    
while True:
    forward_distance = forward_sensor.distance #LOOK HERE I DON'T KNOW IF THERE IS A BRACKET HERE MAYBE YES MAYBE NO
    left_distance = left_sensor.distance
    right_distance = right_senor.distance
    back_distance
    


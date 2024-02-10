import cv2
import numpy as np
import tflite_runtime.interpreter as tf
import RPi.GPIO as GPIO
import time
from newmotor import * #remember on last line and line 46 for more filling in stuff

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BOARD)

interpreter_model = None
pwm_speed = None

camera_resolution = [ , ]  # enter in stuff
servo_to_camera_ratio = 90 / 15  # Ratio of gun and servo. Can be changed depending on design

prev_positions = []  # DO NOT CHNGE

def load_model(model_path):
    interpreter_model = tf.Interpreter(model_path=model_path)
    interpreter_model.allocate_tensors()
    return interpreter_model

def preprocess_image(image):
    tensor_input = image.astype(np.float32)
    input_tensor = np.expand_dims(tensor_input, axis=0)
    return input_tensor

def run_inference(image):
    input_data = preprocess_image(image)
    interpreter_model.set_tensor(interpreter_model.get_input_details()[0]['index'], input_data)
    interpreter_model.invoke()
    return interpreter_model.get_tensor(interpreter_model.get_output_details()[0]['index'])

def calculate_motor_output(target_position_x):
    image_width = camera_resolution[0]  # Get the width of the image
    distance_from_center = target_position_x - image_width / 2
    min_speed = 0.3  # Minimum speed when the target is close
    max_speed = 1.0  # Maximum speed when the target is far away
    scaled_speed = max(min_speed, max_speed - abs(distance_from_center) / (image_width / 2) * (max_speed - min_speed))
    pwm_range = 100
    return int(scaled_speed * pwm_range)

def move_servo(servo_pin, target_position_y):
    GPIO.setup(servo_pin, GPIO.OUT)
    servo = GPIO.PWM(servo_pin, 100)  # I don't know if 100 is good. customizes, I guess?
    servo.start(0)

    servo_movement = target_position_y / servo_to_camera_ratio

    duty_cycle = 7.5 + (0.05 * servo_movement) Check some servo docs
    servo.ChangeDutyCycle(duty_cycle)
    time.sleep(0.1)

def move_motor(yaw, pwm_speed):
    mix(yaw, pwm_speed)

def operation_kill_undead(model_path, servo_pin):
    interpreter_model = load_model(model_path)

    cap = cv2.VideoCapture(0)  
    camera_resolution = [cap.get(cv2.CAP_PROP_FRAME_WIDTH), cap.get(cv2.CAP_PROP_FRAME_HEIGHT)]

    try:
        while True:
            ret, frame = cap.read()
            
            output_data = run_inference(frame)

            if output_data:
                x, y, w, h = output_data[0]
                target_position_x = x + w // 2
                target_position_y = y + h // 2

                offset = 2
                target_position_y += offset

                pwm_speed = calculate_motor_output(target_position_x)
                move_motor(0, pwm_speed)
                move_servo(servo_pin, target_position_y)

    finally:
        cap.release()

operation_kill_undead( , servo_pin) #put model path

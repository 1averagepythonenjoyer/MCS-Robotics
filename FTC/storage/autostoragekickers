private void kickServo(Servo servo) {
    ElapsedTime stopwatch = new ElapsedTime();

    // Extend
    servo.setPosition(STARTING_POSITION + KICK_POSITION);
    stopwatch.reset();
    while (stopwatch.seconds() < DELAY_KICKER_STAYING_UP) {
        idle();
    }

    // Retract
    servo.setPosition(STARTING_POSITION);
    stopwatch.reset();
    while (stopwatch.seconds() < DELAY_BETWEEN_KICKS) {
        idle();
    }
}

private void runKickerSequence() {
        kickServo(storageServo1);
        kickServo(storageServo2);
        kickServo(storageServo3);
}

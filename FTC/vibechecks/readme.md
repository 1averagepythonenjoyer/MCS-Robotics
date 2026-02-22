Bugs I Found That You Should Fix



1. Turret holdPosition() mode conflict (will cause sticky behavior):
When updateManual() calls holdPosition(), it switches to RUN_TO_POSITION. But the next frame when the stick is pushed, it calls setVelocity() without switching back to RUN_USING_ENCODER. The SDK will ignore the velocity command. Fix by adding this line at the top of updateManual() before the velocity call:
javaif (turretMotor.getMode() == DcMotor.RunMode.RUN_TO_POSITION) {
    turretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
}



2. ShooterHood TIME_FACTOR makes all angles identical:
TIME_FACTOR = 0.2 means 3° = 0.6s, 5° = 1.0s, 8° = 1.6s, 11° = 2.2s. But MAX_SERVO_TIME = 0.220s clamps everything above ~1.1° to the same 0.22s. So your DPAD presets (3°, 5°, 8°, 11°) all produce identical servo travel. Either TIME_FACTOR needs to be much smaller (like 0.02), or MAX_SERVO_TIME needs to be larger, depending on your actual servo range.
3. Minor: The HOOD_ANGLE_SHOOT_POSE = 0.0 in TeleOp means the right trigger "fire at calibrated angle" currently does nothing (0° × 0.2 = 0s of travel). Fill this in after calibration.



Each test file has detailed comments at the top explaining exactly what to watch for and what to fix if something is wrong. Run them one at a time, fix issues, then move to the full TeleOp.

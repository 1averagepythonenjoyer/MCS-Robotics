import com.qualcomm.hardware.dfrobot.HuskyLens;
import org.firstinspires.ftc.robotcore.internal.system.Deadline;
import java.util.concurrent.TimeUnit;



private HuskyLens huskyLens;
// Optical Constants for HuskyLens (OV2640)
// F_PX = (F_mm * ImageWidth_px) / SensorWidth_mm
// F_PX = (4.6 * 320) / 3.52 = 418.18
final double FOCAL_LENGTH_PX = 418.18; 
final double REAL_TAG_WIDTH_MM = 100.0; // 10cm DECODE tags
final int SCREEN_CENTER_X = 160;



//add to runOpMode

huskyLens = hardwareMap.get(HuskyLens.class, "huskylens");
// Important: Set the deadline for I2C communication to avoid lag
Deadline rateLimit = new Deadline(1, TimeUnit.SECONDS); 
rateLimit.expire(); 

if (!huskyLens.knock()) {
    telemetry.addData(">>", "Problem communicating with " + huskyLens.getDeviceName());
} else {
    telemetry.addData(">>", "Press start to continue");
}
huskyLens.selectAlgorithm(HuskyLens.Algorithm.TAG_RECOGNITION);





//add to bottom of code

private void UPDATE_TAG_DATA() {
    HuskyLens.Block[] blocks = huskyLens.blocks();
    
    if (blocks.length > 0) {
        // We only care about the first tag seen for now
        HuskyLens.Block tag = blocks[0];

        // --- DISTANCE CALCULATION ---
        // formula: Z = (W_real * f_pixel) / W_pixel
        double distance_mm = (REAL_TAG_WIDTH_MM * FOCAL_LENGTH_PX) / tag.width;

        // --- BEARING CALCULATION ---
        // formula: theta = atan(offset / f)
        double offset_px = tag.x - SCREEN_CENTER_X;
        double bearing_deg = Math.toDegrees(Math.atan2(offset_px, FOCAL_LENGTH_PX));

        // --- TELEMETRY ---
        telemetry.addData("Tag ID", tag.id);
        telemetry.addData("Distance (mm)", "%.1f", distance_mm);
        telemetry.addData("Bearing (deg)", "%.1f", bearing_deg);
        
        // Optional: Save these to global variables if you want to use them for logic
        // target_distance = distance_mm;
        // target_bearing = bearing_deg;
    } else {
        telemetry.addData("HuskyLens", "No Tag Detected");
    }
}


//add to while (opmodeisActive()) loop 

while (opModeIsActive()) {
    MECANUM_DRIVE_VELOCITY(); // Your existing drive code
    UPDATE_TAG_DATA();        // The new vision code
    telemetry.update();
}








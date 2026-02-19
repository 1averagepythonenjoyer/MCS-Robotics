package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.internal.system.Deadline;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Autonomous(name = "Sensor: HuskyLens", group = "Sensor")
public class MySensorHuskyLens extends LinearOpMode {

    // -------------------------------------------------------------------------
    // Inner class: TagData
    // Container that holds everything we know about one detected tag.
    // -------------------------------------------------------------------------
    static class TagData {
        enum TagType { PATTERN, NAVIGATION, UNKNOWN }

        TagType type;
        int id;
        String pattern;      // populated for PATTERN tags (IDs 1-3)
        double distanceMm;   // populated for NAVIGATION tags (IDs 4-5)
        double bearingDeg;
        double[] vector;
    }

    // -------------------------------------------------------------------------
    // Optical Constants for HuskyLens (OV2640)
    // F_PX = (F_mm * ImageWidth_px) / SensorWidth_mm
    // F_PX = (4.6 * 320) / 3.52 = 418.18
    // -------------------------------------------------------------------------
    final double FOCAL_LENGTH_PX   = 418.18;
    final double REAL_TAG_WIDTH_MM = 100.0;  // 10cm DECODE tags
    final int    SCREEN_CENTER_X   = 160;

    private final int READ_PERIOD = 1;
    private HuskyLens huskyLens;

    // -------------------------------------------------------------------------
    // computeVector — unchanged from original, bug fixed (sin not cos for Y)
    // -------------------------------------------------------------------------
    public double[] computeVector(double dist, double angle) {
        double rad = Math.toRadians(angle);
        return new double[]{
            dist * Math.cos(rad),  // X component
            dist * Math.sin(rad)   // Y component
        };
    }

    // -------------------------------------------------------------------------
    // readTags — processes all visible blocks and returns a List<TagData>
    // -------------------------------------------------------------------------
    private List<TagData> readTags(HuskyLens.Block[] blocks) {
        String[] ids = {"gpp", "pgp", "ppg"};
        List<TagData> results = new ArrayList<>();

        for (HuskyLens.Block block : blocks) {
            TagData tag = new TagData();
            tag.id = block.id;

            if (block.id >= 1 && block.id <= 3) {
                // --- Pattern tag ---
                tag.type    = TagData.TagType.PATTERN;
                tag.pattern = ids[block.id - 1];

            } else if (block.id == 4 || block.id == 5) {
                // --- Navigation tag ---
                // Distance: Z = (W_real * f_pixel) / W_pixel
                tag.type       = TagData.TagType.NAVIGATION;
                tag.distanceMm = (REAL_TAG_WIDTH_MM * FOCAL_LENGTH_PX) / block.width;

                // Bearing: theta = atan(offset / f)
                double offsetPx = block.x - SCREEN_CENTER_X;
                tag.bearingDeg  = Math.toDegrees(Math.atan2(offsetPx, FOCAL_LENGTH_PX));

                tag.vector = computeVector(tag.distanceMm, tag.bearingDeg);

            } else {
                tag.type = TagData.TagType.UNKNOWN;
            }

            results.add(tag);
        }
        return results;
    }

    // -------------------------------------------------------------------------
    // OpMode entry point
    // -------------------------------------------------------------------------
    @Override
    public void runOpMode() {
        huskyLens = hardwareMap.get(HuskyLens.class, "huskydyppy");

        Deadline rateLimit = new Deadline(READ_PERIOD, TimeUnit.SECONDS);
        rateLimit.expire();

        if (!huskyLens.knock()) {
            telemetry.addData(">>", "Problem communicating with " + huskyLens.getDeviceName());
        } else {
            telemetry.addData(">>", "Press start to continue");
        }

        huskyLens.selectAlgorithm(HuskyLens.Algorithm.TAG_RECOGNITION);
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            if (!rateLimit.hasExpired()) continue;
            rateLimit.reset();

            List<TagData> tags = readTags(huskyLens.blocks());
            telemetry.addData("Tags detected", tags.size());

            for (TagData tag : tags) {
                if (tag.type == TagData.TagType.PATTERN) {
                    telemetry.addData("Pattern tag [" + tag.id + "]", tag.pattern);

                } else if (tag.type == TagData.TagType.NAVIGATION) {
                    telemetry.addData("Nav tag [" + tag.id + "] distance (mm)", "%.1f", tag.distanceMm);
                    telemetry.addData("Nav tag [" + tag.id + "] bearing (deg)", "%.1f", tag.bearingDeg);
                    telemetry.addData("Nav tag [" + tag.id + "] vector X (mm)", "%.1f", tag.vector[0]);
                    telemetry.addData("Nav tag [" + tag.id + "] vector Y (mm)", "%.1f", tag.vector[1]);
                }
            }

            telemetry.update();
        }
    }
}

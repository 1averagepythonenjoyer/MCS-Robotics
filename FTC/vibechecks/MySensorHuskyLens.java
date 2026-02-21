package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.internal.system.Deadline;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MySensorHuskyLens {
    private final HuskyLens huskyLens;
    private final int READ_PERIOD = 1;
    // Inner class: TagData
    static class TagData {
        enum TagType { PATTERN, NAVIGATION, UNKNOWN }
        TagType type;
        int id;
        String pattern;      // populated for PATTERN tags (IDs 1-3)
        double distanceMm;   // populated for NAVIGATION tags (IDs 4-5)
        double bearingDeg;
        double[] vector;
        @Override
        public String toString() {
            switch (type) {
                case PATTERN:    return "Pattern [" + id + "]: " + pattern;
                case NAVIGATION: return String.format("Nav [%d]: %.1fmm @ %.1f°", id, distanceMm, bearingDeg);
                default:         return "Unknown [" + id + "]";
            }
        }
    }
    // -------------------------------------------------------------------------
    // Optical Constants for HuskyLens (OV2640)
    // F_PX = (F_mm * ImageWidth_px) / SensorWidth_mm
    // F_PX = (4.6 * 320) / 3.52 = 418.18
    // -------------------------------------------------------------------------
    final double FOCAL_LENGTH_PX   = 418.18;
    final double REAL_TAG_WIDTH_MM = 100.0;  // 10cm DECODE tags
    final int    SCREEN_CENTER_X   = 160;
    private double[] computeVector(double dist, double angle) {
        double rad = Math.toRadians(angle);
        return new double[]{
            dist * Math.cos(rad),  // X component
            dist * Math.sin(rad)   // Y component
        };
    }

    public HuskyLensReader(HardwareMap hwMap, String deviceName) {
        huskyLens = hwMap.get(HuskyLens.class, deviceName);
        huskyLens.selectAlgorithm(HuskyLens.Algorithm.TAG_RECOGNITION);
    }
    public boolean isConnected() {
        return huskyLens.knock();
    }
    // -------------------------------------------------------------------------
    // readTags — processes all visible blocks and returns a List<TagData>
    // -------------------------------------------------------------------------
    private List<TagData> readTags() {
        String[] ids = {"gpp", "pgp", "ppg"};
        List<TagData> results = new ArrayList<>();
        HuskyLens.Block[] blocks = huskyLens.blocks()

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

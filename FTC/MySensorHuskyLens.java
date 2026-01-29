/*
Copyright (c) 2023 FIRST

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of FIRST nor the names of its contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.internal.system.Deadline;

import java.util.concurrent.TimeUnit;

@Autonomous(name = "Sensor: HuskyLens", group = "Sensor")

public class MySensorHuskyLens extends LinearOpMode {

    private final int READ_PERIOD = 1;

    private HuskyLens huskyLens;
    // Optical Constants for HuskyLens (OV2640)
    // F_PX = (F_mm * ImageWidth_px) / SensorWidth_mm
    // F_PX = (4.6 * 320) / 3.52 = 418.18
    final double FOCAL_LENGTH_PX = 418.18; 
    final double REAL_TAG_WIDTH_MM = 100.0; // 10cm DECODE tags
    final int SCREEN_CENTER_X = 160;
    
    public double[] computeVector(int dist, int angle)
    {
        double[] vector = {dist * Math.cos(Math.toRadians(angle)), dist * Math.cos(Math.toRadians(90-angle))};
        return vector;
    }
    
    @Override
    public void runOpMode()
    {
        huskyLens = hardwareMap.get(HuskyLens.class, "huskydyppy");
        String[] ids = {"gpp","pgp","ppg"};
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
        while(opModeIsActive()) {
            if (!rateLimit.hasExpired()) {
                continue;
            }
            rateLimit.reset();
            HuskyLens.Block[] blocks = huskyLens.blocks();
            telemetry.addData("Block count", blocks.length);
            for (int i = 0; i < blocks.length; i++) {
                telemetry.addData("Block", blocks[i].toString());
                if (blocks[i].id <= 3 && blocks[i].id > 0) {
                    telemetry.addData("Pattern", ids[blocks[i].id-1]);
                }
                else if (blocks[i].id == 4 || blocks[i].id == 5) {
                    // --- DISTANCE CALCULATION ---
                    // formula: Z = (W_real * f_pixel) / W_pixel
                    double distance_mm = (REAL_TAG_WIDTH_MM * FOCAL_LENGTH_PX) / blocks[i].width;
            
                    // --- BEARING CALCULATION ---
                    // formula: theta = atan(offset / f)
                    double offset_px = blocks[i].x - SCREEN_CENTER_X;
                    double bearing_deg = Math.toDegrees(Math.atan2(offset_px, FOCAL_LENGTH_PX));
            
                    // --- TELEMETRY ---
                    telemetry.addData("Tag ID", tag.id);
                    telemetry.addData("Distance (mm)", "%.1f", distance_mm);
                    telemetry.addData("Bearing (deg)", "%.1f", bearing_deg);
                    
                    // Save these to global variables?
                    // target_distance = distance_mm;
                    // target_bearing = bearing_deg;
                    double[] vector_to_tag = computeVector(distance_mm, bearing_deg)
                    telemetry.addData("Vector to tag:", vector_to_tag)
                }
            }

            telemetry.update();
        }
    }
}

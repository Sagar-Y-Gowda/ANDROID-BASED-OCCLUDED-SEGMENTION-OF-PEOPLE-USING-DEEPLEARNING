package com.aspire.inno.segmentation;

import android.util.Log;

import com.aspire.inno.common.ModelConfig;
import com.aspire.inno.common.ModelInfo;
import com.aspire.inno.common.Shape;
import com.xiaomi.mace.MaceJni;

public class ModelSelector {
    private static int EXPECTED_EXEC_TIME = 1000;
    private static int BASE_CPU_EXEC_TIME = 31;

    private static String CPU_DEVICE = "CPU";
    private static String GPU_DEVICE = "GPU";
    private static String DSP_DEVICE = "DSP";

    public static ModelInfo[] modelInfos = new ModelInfo[] {
            new ModelInfo("deeplab_v3_plus_mobilenet_v2_quant",
                    new String[]{"Input"},
                    new Shape[]{new Shape(new int[]{1, 513, 513, 3})},
                    new String[]{"ResizeBilinear_1"},
                    new Shape[]{new Shape(new int[]{1, 513, 513, 2})},
                    465, true, false),
            new ModelInfo("deeplab_v3_plus_mobilenet_v2",
                    new String[]{"Input"},
                    new Shape[]{new Shape(new int[]{1, 513, 513, 3})},
                    new String[]{"ResizeBilinear_1"},
                    new Shape[]{new Shape(new int[]{1, 513, 513, 2})},
                    465, false, false),
    };

    public ModelConfig select() {
        int modelSize = modelInfos.length;
        float[] gpuPerf = null;
        float[] cpuPerf = null;
        ModelInfo selectedModelInfo = null;
        String deviceType = null;
        for (int i = 0; i < modelSize; ++i) {
            if (modelInfos[i].isQuantized8DSP()) {
            } else if (modelInfos[i].isQuantized8CPU()) {
                if (cpuPerf == null) {
                    cpuPerf = MaceJni.getDeviceCapability(CPU_DEVICE, 1.f);
                }
                float estimatedTime = estimatedExecTime(modelInfos[i].getBasedCPUExecTime(),
                        cpuPerf[1]);
                if (estimatedTime < EXPECTED_EXEC_TIME) {
                    selectedModelInfo = modelInfos[i];
                    deviceType = CPU_DEVICE;
                    break;
                }
            } else {
                if (gpuPerf == null) {
                    gpuPerf = MaceJni.getDeviceCapability(GPU_DEVICE, BASE_CPU_EXEC_TIME);
                    Log.i("Segmentation", "GPU performance " + Float.toString(gpuPerf[0]));
                }
                if (gpuPerf[0] > 0.f) {
                    float estimatedTime = modelInfos[i].getBasedCPUExecTime() * gpuPerf[0];
                    Log.i("Segmentation", "GPU estimated time " + Float.toString(estimatedTime));
                    if (estimatedTime < EXPECTED_EXEC_TIME) {
                        selectedModelInfo = modelInfos[i];
                        deviceType = GPU_DEVICE;
                        break;
                    }
                }
                if (cpuPerf == null) {
                    cpuPerf = MaceJni.getDeviceCapability(CPU_DEVICE, 1.f);
                }
                float estimatedTime = estimatedExecTime(modelInfos[i].getBasedCPUExecTime(),
                        cpuPerf[0]);
                Log.i("Segmentation", "CPU estimated time " + Float.toString(estimatedTime));
                if (estimatedTime < EXPECTED_EXEC_TIME) {
                    selectedModelInfo = modelInfos[i];
                    deviceType = CPU_DEVICE;
                    break;
                }
            }
        }
        if (selectedModelInfo != null) {
            Log.i("Segmentation", "Use model " + selectedModelInfo.getName()
                    + " with device " + deviceType);
            return new ModelConfig(selectedModelInfo, deviceType);
        }
        return null;
    }

    private float estimatedExecTime(final float modelBaseRunTime, final float targetTestRunTime) {
        return modelBaseRunTime * (targetTestRunTime / (float)BASE_CPU_EXEC_TIME);
    }
}

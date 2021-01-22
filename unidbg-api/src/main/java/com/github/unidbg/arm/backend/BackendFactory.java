package com.github.unidbg.arm.backend;

import com.github.unidbg.Emulator;
import com.github.unidbg.arm.backend.dynarmic.Dynarmic;
import com.github.unidbg.arm.backend.hypervisor.Hypervisor;
import com.sun.jna.NativeLoader;

public class BackendFactory {

    static {
        NativeLoader.loadAppleSilicon();
    }

    public static Backend createBackend(Emulator<?> emulator, boolean is64Bit) {
        boolean useHypervisor = Hypervisor.isUseHypervisor();
        if (useHypervisor) {
            Backend backend = HypervisorBackend.tryInitialize(emulator, is64Bit);
            if (backend != null) {
                Hypervisor.onBackendInitialized();
                return backend;
            } else if (Hypervisor.isForceUseHypervisor()) {
                throw new IllegalStateException("Initialize hypervisor backend failed");
            }
        }

        boolean useDynarmic = Dynarmic.isUseDynarmic();
        if (useDynarmic) {
            Backend backend = DynarmicBackend.tryInitialize(emulator, is64Bit);
            if (backend != null) {
                Dynarmic.onBackendInitialized();
                return backend;
            } else if (Dynarmic.isForceUseDynarmic()) {
                throw new IllegalStateException("Initialize dynarmic backend failed");
            }
        }
        return new UnicornBackend(emulator, is64Bit);
    }

}

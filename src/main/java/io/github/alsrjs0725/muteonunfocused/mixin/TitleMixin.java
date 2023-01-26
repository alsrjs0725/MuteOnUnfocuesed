package io.github.alsrjs0725.muteonunfocused.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.logging.Logger;

@Mixin(TitleScreen.class)
public class TitleMixin {

    private Logger logger = Logger.getLogger("MuteOnUnFocused");
    private boolean prv = true;
    private SimpleOption<Double> volumeOption;
    private double prvVol;
    private MinecraftClient MCCli;
    private boolean isInit = true;


    @Inject(at=@At("RETURN"), method="initWidgetsNormal"   )
    private void runTask(int y, int spacingY, CallbackInfo ci) {
        if (!isInit) { return; }
        isInit = false;
        MCCli = MinecraftClient.getInstance();
        volumeOption = MCCli.options.getSoundVolumeOption(SoundCategory.MASTER);
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(100);
                    if (prv == MCCli.isWindowFocused()) { continue; }
                    if (!prv) {  // Window is Focused
                        volumeOption.setValue(prvVol);
                        prv = true;
                    } else {
                        prvVol = volumeOption.getValue();
                        volumeOption.setValue(0.);
                        prv = false;
                    }
                } catch (Exception e) {
                    logger.warning(e.getMessage());
                }
            }
        }).start();
    }
}

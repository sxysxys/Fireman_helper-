package com.rescue.hc.lib.util;

import com.rescue.hc.lib.util.effects.BaseEffects;
import com.rescue.hc.lib.util.effects.FadeIn;
import com.rescue.hc.lib.util.effects.FlipH;
import com.rescue.hc.lib.util.effects.FlipV;
import com.rescue.hc.lib.util.effects.NewsPaper;
import com.rescue.hc.lib.util.effects.SideFall;
import com.rescue.hc.lib.util.effects.SlideLeft;
import com.rescue.hc.lib.util.effects.SlideRight;
import com.rescue.hc.lib.util.effects.SlideTop;

/*
 * Copyright 2014 litao
 * https://github.com/sd6352051/NiftyDialogEffects
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public enum EffectsType {

    Fadein(FadeIn.class),
    Slideleft(SlideLeft.class),
    Slidetop(SlideTop.class),
    SlideBottom(com.rescue.hc.lib.util.effects.SlideBottom.class),
    Slideright(SlideRight.class),
    Fall(com.rescue.hc.lib.util.effects.Fall.class),
    Newspager(NewsPaper.class),
    Fliph(FlipH.class),
    Flipv(FlipV.class),
    RotateBottom(com.rescue.hc.lib.util.effects.RotateBottom.class),
    RotateLeft(com.rescue.hc.lib.util.effects.RotateLeft.class),
    Slit(com.rescue.hc.lib.util.effects.Slit.class),
    Shake(com.rescue.hc.lib.util.effects.Shake.class),
    Sidefill(SideFall.class);
    private Class<? extends BaseEffects> effectsClazz;

    private EffectsType(Class<? extends BaseEffects> mclass) {
        effectsClazz = mclass;
    }

    public BaseEffects getAnimator() {
        BaseEffects bEffects = null;
        try {
            bEffects = effectsClazz.newInstance();
        } catch (ClassCastException e) {
            throw new Error("Can not init animatorClazz instance");
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            throw new Error("Can not init animatorClazz instance");
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            throw new Error("Can not init animatorClazz instance");
        }
        return bEffects;
    }
}

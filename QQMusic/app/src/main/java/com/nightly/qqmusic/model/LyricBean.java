package com.nightly.qqmusic.model;

/**
 * Created by Nightly on 2016/10/8.
 */

public class LyricBean {

    /**
     * showapi_res_code : 0
     * showapi_res_error :
     * showapi_res_body : {"ret_code":0,"lyric":"","lyric_txt":"                                 丑八怪      《如果我爱你》电视剧插曲            薛之谦      词：甘世佳      曲：李荣浩      编曲：李荣浩            如果世界漆黑   其实我很美            在爱情里面进退   最多被消费            无关痛痒的是非      又怎么不对   无所谓            如果像你一样   总有人赞美            围绕着我的卑微   也许能消退            其实我并不在意   有很多机会            像巨人一样的无畏      放纵我   心里的鬼      可是我不配            丑八怪   能否别把灯打开            我要的爱   出没在      漆黑一片的舞台            丑八怪   在这暧昧的时代            我的存在   像意外            有人用一滴泪   会红颜祸水            有人丢掉称谓   什么也不会            只要你足够虚伪   就不怕魔鬼   对不对            如果剧本写好   谁比谁高贵            我只能沉默以对   美丽本无罪            当欲望开始贪杯   有更多机会      像尘埃一样的无畏   化成灰   谁认得谁      管他配不配            丑八怪   能否别把灯打开            我要的爱   出没在      漆黑一片的舞台            丑八怪   在这暧昧的时代            我的存在   不意外            丑八怪   其实见多就不怪            放肆去high   用力踩      那不堪一击的洁白            丑八怪   这是我们的时代            我不存在   才意外"}
     */

    private int showapi_res_code;
    private String showapi_res_error;
    /**
     * ret_code : 0
     * lyric :
     * lyric_txt :                                  丑八怪      《如果我爱你》电视剧插曲            薛之谦      词：甘世佳      曲：李荣浩      编曲：李荣浩            如果世界漆黑   其实我很美            在爱情里面进退   最多被消费            无关痛痒的是非      又怎么不对   无所谓            如果像你一样   总有人赞美            围绕着我的卑微   也许能消退            其实我并不在意   有很多机会            像巨人一样的无畏      放纵我   心里的鬼      可是我不配            丑八怪   能否别把灯打开            我要的爱   出没在      漆黑一片的舞台            丑八怪   在这暧昧的时代            我的存在   像意外            有人用一滴泪   会红颜祸水            有人丢掉称谓   什么也不会            只要你足够虚伪   就不怕魔鬼   对不对            如果剧本写好   谁比谁高贵            我只能沉默以对   美丽本无罪            当欲望开始贪杯   有更多机会      像尘埃一样的无畏   化成灰   谁认得谁      管他配不配            丑八怪   能否别把灯打开            我要的爱   出没在      漆黑一片的舞台            丑八怪   在这暧昧的时代            我的存在   不意外            丑八怪   其实见多就不怪            放肆去high   用力踩      那不堪一击的洁白            丑八怪   这是我们的时代            我不存在   才意外
     */

    private ShowapiResBodyBean showapi_res_body;

    public int getShowapi_res_code() {
        return showapi_res_code;
    }

    public void setShowapi_res_code(int showapi_res_code) {
        this.showapi_res_code = showapi_res_code;
    }

    public String getShowapi_res_error() {
        return showapi_res_error;
    }

    public void setShowapi_res_error(String showapi_res_error) {
        this.showapi_res_error = showapi_res_error;
    }

    public ShowapiResBodyBean getShowapi_res_body() {
        return showapi_res_body;
    }

    public void setShowapi_res_body(ShowapiResBodyBean showapi_res_body) {
        this.showapi_res_body = showapi_res_body;
    }

    public static class ShowapiResBodyBean {
        private int ret_code;
        private String lyric;
        private String lyric_txt;

        public int getRet_code() {
            return ret_code;
        }

        public void setRet_code(int ret_code) {
            this.ret_code = ret_code;
        }

        public String getLyric() {
            return lyric;
        }

        public void setLyric(String lyric) {
            this.lyric = lyric;
        }

        public String getLyric_txt() {
            return lyric_txt;
        }

        public void setLyric_txt(String lyric_txt) {
            this.lyric_txt = lyric_txt;
        }
    }
}

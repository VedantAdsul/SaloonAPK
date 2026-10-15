package com.mountreachsolution.SaloonAPK;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SuggestionRepository {

    public static List<Suggestion> getHairSuggestions(String faceShape, String gender) {
        List<Suggestion> list = new ArrayList<>();
        String g = gender.toLowerCase(Locale.ROOT);

        switch (faceShape) {
            case "Square":
                if (g.equals("female")) {
                    list.add(new Suggestion("Soft Waves", R.drawable.img_4));
                    list.add(new Suggestion("Layered Bob", R.drawable.img_5));
                    list.add(new Suggestion("Side Part", R.drawable.img_6));
                } else {
                    list.add(new Suggestion("Textured Crop", R.drawable.img));
                    list.add(new Suggestion("Buzz Cut", R.drawable.img_22));
                    list.add(new Suggestion("High Volume Quiff", R.drawable.img_23));
                }
                break;
            case "Rectangle":
                if (g.equals("female")) {
                    list.add(new Suggestion("Curtain Bangs", R.drawable.img_7));
                    list.add(new Suggestion("Shoulder-length", R.drawable.img_8));
                    list.add(new Suggestion("Blowout", R.drawable.img_9));
                } else {
                    list.add(new Suggestion("Comb-over", R.drawable.img_24));
                    list.add(new Suggestion("Layered Medium", R.drawable.img_25));
                    list.add(new Suggestion("Side Part", R.drawable.img_26));
                }
                break;
            case "Round":
                if (g.equals("female")) {
                    list.add(new Suggestion("Long Layers", R.drawable.img_10));
                    list.add(new Suggestion("Pixie Cut", R.drawable.img_11));
                    list.add(new Suggestion("Side Bangs", R.drawable.img_12));
                } else {
                    list.add(new Suggestion("Pompadour", R.drawable.img_27));
                    list.add(new Suggestion("Faux Hawk", R.drawable.img_28));
                    list.add(new Suggestion("Side Fade", R.drawable.img_29));
                }
                break;
            case "Heart":
                if (g.equals("female")) {
                    list.add(new Suggestion("Chin-length Bob", R.drawable.img_13));
                    list.add(new Suggestion("Side-swept Fringe", R.drawable.img_14));
                    list.add(new Suggestion("Soft Curls", R.drawable.img_15));
                } else {
                    list.add(new Suggestion("Side-swept Fringe", R.drawable.img_30));
                    list.add(new Suggestion("Medium Waves", R.drawable.img_31));
                    list.add(new Suggestion("Textured Quiff", R.drawable.img_32));
                }
                break;
            case "Diamond":
                if (g.equals("female")) {
                    list.add(new Suggestion("Chin-length Layers", R.drawable.img_16));
                    list.add(new Suggestion("Side Part", R.drawable.img_17));
                    list.add(new Suggestion("Pulled-back Pony", R.drawable.img_18));
                } else {
                    list.add(new Suggestion("Messy Quiff", R.drawable.img_33));
                    list.add(new Suggestion("Side-swept", R.drawable.img_34));
                    list.add(new Suggestion("Medium Layers", R.drawable.img_35));
                }
                break;
            default:
                if (g.equals("female")) {
                    list.add(new Suggestion("Balanced Layers", R.drawable.img_19));
                    list.add(new Suggestion("Blunt Bob", R.drawable.img_20));
                    list.add(new Suggestion("Center Part", R.drawable.img_21));
                } else {
                    list.add(new Suggestion("Classic Quiff", R.drawable.img_36));
                    list.add(new Suggestion("Side Part Flow", R.drawable.img_37));
                    list.add(new Suggestion("Textured Crop", R.drawable.img_38));
                }
                break;
        }
        return list;
    }

    public static List<Suggestion> getBeardSuggestions(String faceShape) {
        List<Suggestion> list = new ArrayList<>();

        switch (faceShape) {
            case "Square":
                list.add(new Suggestion("Light Stubble", R.drawable.img_39));
                    list.add(new Suggestion("Short Boxed", R.drawable.img_40));
                break;
            case "Rectangle":
                list.add(new Suggestion("Fuller Chin", R.drawable.img_41));
                list.add(new Suggestion("Short Sides", R.drawable.img_42));
                break;
            case "Round":
                list.add(new Suggestion("Angular Boxed", R.drawable.img_43));
                list.add(new Suggestion("Defined Lines", R.drawable.img_44));
                break;
            case "Heart":
                list.add(new Suggestion("Short Boxed", R.drawable.img_40));
                list.add(new Suggestion("Goatee", R.drawable.img_45));
                break;
            case "Diamond":
                list.add(new Suggestion("Chin Curtain", R.drawable.img_46));
                list.add(new Suggestion("Goatee Variants", R.drawable.img_47));
                break;
            default:
                list.add(new Suggestion("Any Style", R.drawable.img_48));
                list.add(new Suggestion("Full Beard", R.drawable.img_49));
                break;
        }
        return list;
    }
}
package com.zsh.teach_oa;

import org.junit.Test;

public class SwitchTest {


    @Test
    public void test() throws Exception {

        String str = "test";
        switch (str) {
            case "test":
                System.out.println("a");
                break;
            case "b":
                System.out.println("b");
                break;
            case "c":
                System.out.println("c");
                break;
            default:
                System.out.println("c");
                break;
        }


    }
}

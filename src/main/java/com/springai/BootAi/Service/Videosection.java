package com.springai.BootAi.Service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
public class Videosection {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;

    private static final int FPS = 24;

    // 5 scenes x 6 seconds = 30 seconds
    private static final int SCENE_DURATION = 6;
    private static final int TOTAL_SCENES = 5;

    private final ChatClient chatClient;

    public Videosection(ChatClient chatClient) {
        this.chatClient = chatClient;
    }


    // =========================================================
    // MAIN VIDEO METHOD
    // =========================================================

    public String createVideo(String topic) throws Exception {

        System.out.println("=================================");
        System.out.println("Creating video for: " + topic);
        System.out.println("=================================");

        // 1. Generate script using Groq
        String script = generateScript(topic);

        System.out.println("\n===== GENERATED SCRIPT =====");
        System.out.println(script);
        System.out.println("============================");


        // 2. Create unique frames directory

        File framesDirectory =
                new File(
                        "animation-frames-"
                                + System.currentTimeMillis()
                );

        if (!framesDirectory.exists()) {

            boolean created =
                    framesDirectory.mkdirs();

            if (!created) {
                throw new RuntimeException(
                        "Could not create frames directory"
                );
            }
        }


        // 3. Create 5 scenes

        String[] scenes =
                parseScenes(script);


        // 4. Generate frames

        int totalFrames =
                FPS
                        * SCENE_DURATION
                        * TOTAL_SCENES;

        System.out.println(
                "Total frames: "
                        + totalFrames
        );


        for (int frameNumber = 0;
             frameNumber < totalFrames;
             frameNumber++) {


            // Which scene?
            int sceneNumber =
                    frameNumber
                            / (FPS * SCENE_DURATION);


            // Frame inside current scene
            int sceneFrame =
                    frameNumber
                            % (FPS * SCENE_DURATION);


            String sceneText;

            if (sceneNumber < scenes.length) {

                sceneText =
                        scenes[sceneNumber];

            } else {

                sceneText =
                        "Action scene";
            }


            // Create PNG frame

            createFrame(
                    frameNumber,
                    topic,
                    sceneNumber + 1,
                    sceneText,
                    sceneFrame,
                    FPS * SCENE_DURATION,
                    framesDirectory
            );


            // Print progress

            if (frameNumber % 100 == 0) {

                System.out.println(
                        "Creating frame "
                                + frameNumber
                                + " / "
                                + totalFrames
                );
            }
        }


        // 5. Create output MP4

        String outputVideo =
                "animation-"
                        + System.currentTimeMillis()
                        + ".mp4";


        createMp4(
                framesDirectory,
                outputVideo
        );


        String absolutePath =
                new File(outputVideo)
                        .getAbsolutePath();


        System.out.println(
                "\n================================="
        );

        System.out.println(
                "VIDEO CREATED SUCCESSFULLY"
        );

        System.out.println(
                absolutePath
        );

        System.out.println(
                "================================="
        );


        return absolutePath;
    }


    // =========================================================
    // GENERATE AI SCRIPT
    // =========================================================

    public String generateScript(String topic) {

        String prompt = """

                You are a professional animation script writer.

                Create a 30-second animated story about:

                %s

                Create exactly 5 scenes.

                Each scene must contain:

                SCENE 1:
                Action:
                Background:
                Character:
                Camera:
                Dialogue:

                SCENE 2:
                Action:
                Background:
                Character:
                Camera:
                Dialogue:

                Continue until SCENE 5.

                Keep the story simple and suitable for
                a 2D animated video.

                Do not create long paragraphs.

                """.formatted(topic);


        String result =
                chatClient
                        .prompt()
                        .user(prompt)
                        .call()
                        .content();


        if (result == null ||
                result.isBlank()) {

            throw new RuntimeException(
                    "AI did not generate a script"
            );
        }


        return result;
    }


    // =========================================================
    // PARSE AI SCENES
    // =========================================================

    private String[] parseScenes(
            String script) {


        String[] parts =
                script.split(
                        "(?i)SCENE\\s*[1-5]\\s*:"
                );


        if (parts.length <= 1) {

            return new String[]{
                    script,
                    script,
                    script,
                    script,
                    script
            };
        }


        String[] scenes =
                new String[parts.length - 1];


        System.arraycopy(
                parts,
                1,
                scenes,
                0,
                scenes.length
        );


        return scenes;
    }


    // =========================================================
    // CREATE FRAME
    // =========================================================

    private void createFrame(
            int frameNumber,
            String topic,
            int sceneNumber,
            String sceneText,
            int sceneFrame,
            int totalSceneFrames,
            File directory)
            throws IOException {


        BufferedImage image =
                new BufferedImage(
                        WIDTH,
                        HEIGHT,
                        BufferedImage.TYPE_INT_RGB
                );


        Graphics2D graphics =
                image.createGraphics();


        // =====================================================
        // SMOOTH RENDERING
        // =====================================================

        graphics.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );


        // =====================================================
        // BACKGROUND
        // =====================================================

        graphics.setColor(
                new Color(
                        8,
                        12,
                        25
                )
        );

        graphics.fillRect(
                0,
                0,
                WIDTH,
                HEIGHT
        );


        // =====================================================
        // TITLE
        // =====================================================

        graphics.setColor(
                Color.WHITE
        );

        graphics.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        40
                )
        );


        String title =
                topic;


        graphics.drawString(
                title,
                60,
                65
        );


        // =====================================================
        // SCENE
        // =====================================================

        graphics.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        26
                )
        );


        graphics.drawString(
                "SCENE " + sceneNumber,
                60,
                110
        );


        // =====================================================
        // ANIMATION PROGRESS
        // =====================================================

        double progress =
                (double) sceneFrame
                        / totalSceneFrames;


        // =====================================================
        // DRAW SCENE
        // =====================================================

        drawScene(
                graphics,
                topic,
                sceneNumber,
                progress
        );


        // =====================================================
        // SCENE DESCRIPTION BOX
        // =====================================================

        graphics.setColor(
                new Color(
                        0,
                        0,
                        0,
                        190
                )
        );


        graphics.fillRoundRect(
                50,
                500,
                1180,
                140,
                20,
                20
        );


        graphics.setColor(
                Color.WHITE
        );


        graphics.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        22
                )
        );


        drawWrappedText(
                graphics,
                cleanText(sceneText),
                75,
                535,
                1120
        );


        // =====================================================
        // PROGRESS BAR
        // =====================================================

        graphics.setColor(
                new Color(
                        40,
                        170,
                        255
                )
        );


        graphics.fillRect(
                50,
                675,
                (int) (1180 * progress),
                10
        );


        graphics.dispose();


        // =====================================================
        // SAVE PNG
        // =====================================================

        File output =
                new File(
                        directory,
                        String.format(
                                "frame_%05d.png",
                                frameNumber
                        )
                );


        ImageIO.write(
                image,
                "png",
                output
        );
    }


    // =========================================================
    // DRAW SCENE
    // =========================================================

    private void drawScene(
            Graphics2D graphics,
            String topic,
            int sceneNumber,
            double progress) {


        String lowerTopic =
                topic.toLowerCase();


        // =====================================================
        // SPIDER-MAN VS VENOM STYLE
        // =====================================================

        if (lowerTopic.contains("spider")
                && lowerTopic.contains("venom")) {


            drawCityBackground(
                    graphics
            );


            if (sceneNumber % 2 == 0) {

                drawVenom(
                        graphics,
                        800,
                        400,
                        progress
                );

                drawSpiderHero(
                        graphics,
                        300,
                        400,
                        progress
                );

            } else {

                drawSpiderHero(
                        graphics,
                        300,
                        400,
                        progress
                );

                drawVenom(
                        graphics,
                        800,
                        400,
                        progress
                );
            }


            return;
        }


        // =====================================================
        // GENERIC ANIMATION
        // =====================================================

        drawGenericBackground(
                graphics
        );


        int x =
                150
                        + (int)
                        (progress * 850);


        drawGenericCharacter(
                graphics,
                x,
                400
        );
    }


    // =========================================================
    // CITY BACKGROUND
    // =========================================================

    private void drawCityBackground(
            Graphics2D graphics) {


        graphics.setColor(
                new Color(
                        20,
                        25,
                        50
                )
        );


        graphics.fillRect(
                0,
                150,
                WIDTH,
                350
        );


        // Buildings

        graphics.setColor(
                new Color(
                        30,
                        35,
                        55
                )
        );


        for (int x = 0;
             x < WIDTH;
             x += 120) {


            int height =
                    100
                            + (x % 200);


            graphics.fillRect(
                    x,
                    500 - height,
                    100,
                    height
            );
        }


        // Windows

        graphics.setColor(
                new Color(
                        255,
                        220,
                        100
                )
        );


        for (int x = 20;
             x < WIDTH;
             x += 120) {


            for (int y = 250;
                 y < 480;
                 y += 50) {


                graphics.fillRect(
                        x,
                        y,
                        15,
                        20
                );
            }
        }


        // Ground

        graphics.setColor(
                new Color(
                        15,
                        15,
                        20
                )
        );


        graphics.fillRect(
                0,
                500,
                WIDTH,
                220
        );
    }


    // =========================================================
    // SPIDER HERO
    // =========================================================

    private void drawSpiderHero(
            Graphics2D graphics,
            int x,
            int y,
            double progress) {


        // Head

        graphics.setColor(
                Color.RED
        );


        graphics.fillOval(
                x,
                y - 140,
                100,
                100
        );


        // Eyes

        graphics.setColor(
                Color.WHITE
        );


        graphics.fillOval(
                x + 20,
                y - 110,
                25,
                35
        );


        graphics.fillOval(
                x + 55,
                y - 110,
                25,
                35
        );


        // Body

        graphics.setColor(
                new Color(
                        25,
                        60,
                        180
                )
        );


        graphics.fillRoundRect(
                x + 10,
                y - 40,
                80,
                160,
                25,
                25
        );


        // Red chest

        graphics.setColor(
                Color.RED
        );


        graphics.fillOval(
                x + 25,
                y - 25,
                50,
                60
        );


        // Arms

        graphics.setStroke(
                new BasicStroke(
                        14
                )
        );


        graphics.drawLine(
                x + 15,
                y,
                x - 80,
                y - 70
        );


        graphics.drawLine(
                x + 85,
                y,
                x + 170,
                y - 70
        );


        // Legs

        graphics.drawLine(
                x + 30,
                y + 110,
                x - 20,
                y + 200
        );


        graphics.drawLine(
                x + 65,
                y + 110,
                x + 120,
                y + 200
        );


        // Web

        graphics.setColor(
                Color.WHITE
        );


        graphics.setStroke(
                new BasicStroke(
                        3
                )
        );


        graphics.drawLine(
                x + 170,
                y - 70,
                x + 230,
                y - 150
        );
    }


    // =========================================================
    // VENOM
    // =========================================================

    private void drawVenom(
            Graphics2D graphics,
            int x,
            int y,
            double progress) {


        // Head

        graphics.setColor(
                Color.BLACK
        );


        graphics.fillOval(
                x,
                y - 160,
                130,
                130
        );


        // White eyes

        graphics.setColor(
                Color.WHITE
        );


        graphics.fillOval(
                x + 20,
                y - 125,
                35,
                25
        );


        graphics.fillOval(
                x + 75,
                y - 125,
                35,
                25
        );


        // Body

        graphics.setColor(
                Color.BLACK
        );


        graphics.fillRoundRect(
                x + 5,
                y - 40,
                120,
                190,
                30,
                30
        );


        // White chest

        graphics.setColor(
                Color.WHITE
        );


        graphics.fillOval(
                x + 35,
                y + 10,
                60,
                80
        );


        // Arms

        graphics.setColor(
                Color.BLACK
        );


        graphics.setStroke(
                new BasicStroke(
                        18
                )
        );


        graphics.drawLine(
                x + 20,
                y,
                x - 100,
                y - 80
        );


        graphics.drawLine(
                x + 105,
                y,
                x + 210,
                y - 80
        );


        // Legs

        graphics.drawLine(
                x + 35,
                y + 130,
                x - 20,
                y + 220
        );


        graphics.drawLine(
                x + 90,
                y + 130,
                x + 145,
                y + 220
        );


        // Tongue

        graphics.setColor(
                new Color(
                        220,
                        40,
                        80
                )
        );


        graphics.setStroke(
                new BasicStroke(
                        8
                )
        );


        graphics.drawLine(
                x + 65,
                y - 35,
                x + 65,
                y + 30
        );
    }


    // =========================================================
    // GENERIC BACKGROUND
    // =========================================================

    private void drawGenericBackground(
            Graphics2D graphics) {


        graphics.setColor(
                new Color(
                        30,
                        40,
                        60
                )
        );


        graphics.fillRect(
                0,
                150,
                WIDTH,
                350
        );


        graphics.setColor(
                new Color(
                        20,
                        20,
                        25
                )
        );


        graphics.fillRect(
                0,
                500,
                WIDTH,
                220
        );
    }


    // =========================================================
    // GENERIC CHARACTER
    // =========================================================

    private void drawGenericCharacter(
            Graphics2D graphics,
            int x,
            int y) {


        graphics.setColor(
                Color.RED
        );


        graphics.fillOval(
                x,
                y - 120,
                80,
                80
        );


        graphics.setColor(
                new Color(
                        40,
                        100,
                        200
                )
        );


        graphics.fillRoundRect(
                x + 5,
                y - 40,
                70,
                130,
                20,
                20
        );


        graphics.setStroke(
                new BasicStroke(
                        12
                )
        );


        graphics.drawLine(
                x + 10,
                y,
                x - 60,
                y - 50
        );


        graphics.drawLine(
                x + 70,
                y,
                x + 140,
                y - 50
        );
    }


    // =========================================================
    // WRAP TEXT
    // =========================================================

    private void drawWrappedText(
            Graphics2D graphics,
            String text,
            int x,
            int y,
            int maxWidth) {


        String[] words =
                text.split("\\s+");


        StringBuilder line =
                new StringBuilder();


        int currentY = y;


        for (String word : words) {


            String test =
                    line.length() == 0
                            ? word
                            : line + " " + word;


            int width =
                    graphics
                            .getFontMetrics()
                            .stringWidth(
                                    test
                            );


            if (width > maxWidth) {


                graphics.drawString(
                        line.toString(),
                        x,
                        currentY
                );


                currentY += 28;


                line =
                        new StringBuilder(
                                word
                        );


            } else {


                if (line.length() > 0) {
                    line.append(" ");
                }


                line.append(word);
            }
        }


        if (line.length() > 0) {


            graphics.drawString(
                    line.toString(),
                    x,
                    currentY
            );
        }
    }


    // =========================================================
    // CLEAN AI TEXT
    // =========================================================

    private String cleanText(
            String text) {


        return text
                .replace(
                        "\n",
                        " "
                )
                .replace(
                        "Action:",
                        ""
                )
                .trim();
    }


    // =========================================================
    // CREATE MP4 USING FFMPEG
    // =========================================================

    private void createMp4(
            File framesDirectory,
            String outputVideo)
            throws Exception {


        String inputPattern =
                new File(
                        framesDirectory,
                        "frame_%05d.png"
                ).getAbsolutePath();


        ProcessBuilder processBuilder =
                new ProcessBuilder(

                        "ffmpeg",

                        "-y",

                        "-framerate",

                        String.valueOf(FPS),

                        "-i",

                        inputPattern,

                        "-c:v",

                        "libx264",

                        "-pix_fmt",

                        "yuv420p",

                        outputVideo
                );


        processBuilder
                .redirectErrorStream(true);


        Process process =
                processBuilder.start();


        process.getInputStream()
                .transferTo(
                        System.out
                );


        int result =
                process.waitFor();


        if (result != 0) {

            throw new RuntimeException(
                    "FFmpeg failed. Exit code: "
                            + result
            );
        }
    }
}
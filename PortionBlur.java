import static marvin.MarvinPluginCollection.*;

public class PortionBlur {
    public PortionBlur(){
        // 1. Load image
        MarvinImage image = MarvinImageIO.loadImage("./res/credit_card.jpg");

        // 2. Create masks for each blurred region
        MarvinImageMask mask1 = new MarvinImageMask(image.getWidth(), image.getHeight(), 38,170,345,24);
        MarvinImageMask mask2 = new MarvinImageMask(image.getWidth(), image.getHeight(), 52,212,65,24);
        MarvinImageMask mask3 = new MarvinImageMask(image.getWidth(), image.getHeight(), 196,212,65,20);
        MarvinImageMask mask4 = new MarvinImageMask(image.getWidth(), image.getHeight(), 38,240,200,20);

        // 3. Process Image with each mask
        GaussianBlur gaussianBlur = new GaussianBlur();
        gaussianBlur.load();
        gaussianBlur.attributes.set("radius",15);

        gaussianBlur.process(image.clone(), image, mask1);
        gaussianBlur.process(image.clone(), image, mask2);
        gaussianBlur.process(image.clone(), image, mask3);
        gaussianBlur.process(image.clone(), image, mask4);

        // 4. Save the final image
        MarvinImageIO.saveImage(image, "./res/credit_card_out.jpg");
    }
    public static void main(String[] args) {
        new PortionBlur();
        System.exit(0);
    }
}
package gaddam1987.github;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.resource.FSEntityResolver;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

@Configuration
public class SpringTheamyLeafConfiguration {

    private TemplateResolver pdfTemplateResolver() {
        final TemplateResolver classLoaderTemplateResolver = new ClassLoaderTemplateResolver();
        classLoaderTemplateResolver.setPrefix("pdf");
        classLoaderTemplateResolver.setTemplateMode("XHTML");
        classLoaderTemplateResolver.setCharacterEncoding("UTF-8");
        classLoaderTemplateResolver.setSuffix(".html");
        return classLoaderTemplateResolver;
    }

    @Bean
    public SpringTemplateEngine springTemplateEngine() {
        final SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();
        springTemplateEngine.addTemplateResolver(pdfTemplateResolver());
        return springTemplateEngine;
    }

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, DocumentException {
        final AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(SpringTheamyLeafConfiguration.class);
        final SpringTemplateEngine bean = annotationConfigApplicationContext.getBean(SpringTemplateEngine.class);

        final Context ctx = new Context();
        ctx.setVariable("message", "Naresh");
        ctx.setVariable("subscriptionDate", new Date());
        ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
        //ctx.setVariable("imageResourceName", ); // so that we can reference it from HTML
        final String htmlContent = bean.process("/pdf", ctx);


        ByteOutputStream os = new ByteOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        ITextFontResolver fontResolver = renderer.getFontResolver();

       // ClassPathResource regular = new ClassPathResource("/font/LiberationSerif-Regular.ttf");
       // fontResolver.addFont(regular.getURL().toString(), BaseFont.IDENTITY_H, true);

        renderer.getSharedContext().setReplacedElementFactory(new MediaReplacedElementFactory(renderer.getSharedContext().getReplacedElementFactory()));
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(os);

        byte[] pdfAsBytes = os.getBytes();
        os.close();

        FileOutputStream fos = new FileOutputStream(new File("message.pdf"));
        fos.write(pdfAsBytes);
        fos.close();

    }

}

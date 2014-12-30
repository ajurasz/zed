package zed.service.attachment.file.routing

import com.google.common.io.Files
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.rest.RestBindingMode
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import zed.service.document.mongo.routing.SaveOperation

import static zed.service.attachment.file.routing.CamelGroovy.groovy

@Component
public class AttachmentRestGatewayRoute extends RouteBuilder {

    @Value('${zed.service.attachment.file.path:/tmp}')
    private File storage;

    @Override
    public void configure() throws Exception {

        rest("/api/attachment").
                post("/upload").type(Object.class).route().
                setBody().groovy("new zed.service.attachment.file.routing.UploadOperation(body)").
                to("direct:upload");

        rest("/api/attachment").
                get("/download/{id}").bindingMode(RestBindingMode.off).produces('application/octet-stream').route().
                setBody().groovy("new zed.service.attachment.file.routing.DownloadOperation(headers['id'])").
                to("direct:download");

        // Operations handlers

        from("direct:upload").
                process(groovy { RichExchange exc ->
                    UploadOperation upload = exc.body(UploadOperation.class)
                    Files.write(upload.data(), new File(storage, "tmp_" + exc.id()))
                    exc.body = new SaveOperation('attachment', upload.attachment())
                }).
                to("direct:save").
                process(groovy { RichExchange exc ->
                    new File(storage, "tmp_" + exc.id()).renameTo(new File(storage, exc.body(String.class)))
                });

        from("direct:download").
                process(groovy { RichExchange exc ->
                    DownloadOperation download = exc.body(DownloadOperation.class)
                    exc.body = new FileInputStream(new File(storage, download.id()))
                })

    }

}
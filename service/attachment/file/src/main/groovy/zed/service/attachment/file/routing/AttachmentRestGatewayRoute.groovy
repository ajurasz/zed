package zed.service.attachment.file.routing

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.rest.RestBindingMode
import org.springframework.stereotype.Component
import zed.service.attachment.file.service.BinaryStorage
import zed.service.document.mongo.routing.SaveOperation

@Component
public class AttachmentRestGatewayRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        rest("/api/attachment").
                post("/upload/{collection}").type(Object.class).route().
                setBody().groovy("new zed.service.attachment.file.routing.UploadOperation(body)").
                to("direct:upload")

        rest("/api/attachment").
                get("/download/{id}").bindingMode(RestBindingMode.off).produces('application/octet-stream').route().
                setBody().groovy("new zed.service.attachment.file.routing.DownloadOperation(headers['id'])").
                to("direct:download")

        // Operations handlers

        from("direct:upload").
                process {
                    def exc = new ExchangeContext(it)
                    UploadOperation upload = exc.body(UploadOperation.class)
                    exc.bean(BinaryStorage.class).stageData(exc.id(), upload.data())
                    exc.body = new SaveOperation(exc.stringHeader('collection'), upload.attachment())
                }.
                to("direct:save").
                process {
                    def exc = new ExchangeContext(it)
                    exc.bean(BinaryStorage.class).commitData(exc.id(), exc.body(String.class))
                }

        from("direct:download").
                process {
                    def exc = new ExchangeContext(it)
                    DownloadOperation download = exc.body(DownloadOperation.class)
                    exc.body = exc.bean(BinaryStorage.class).readData(download.id())
                }

    }

}
package ResourceTest;

import io.vertx.core.Vertx;
import makjust.utils.ResourcesInit;
import org.junit.jupiter.api.Test;

class CopyTest {
    private ResourcesInit resourcesInit;
    @Test
    void copyTest() throws Exception{
        resourcesInit=new ResourcesInit(Vertx.vertx());

    }}

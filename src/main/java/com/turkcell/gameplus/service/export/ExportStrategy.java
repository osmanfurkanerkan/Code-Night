package com.turkcell.gameplus.service.export;

import java.io.IOException;

public interface ExportStrategy {
    void export(String outputBasePath) throws IOException;
}

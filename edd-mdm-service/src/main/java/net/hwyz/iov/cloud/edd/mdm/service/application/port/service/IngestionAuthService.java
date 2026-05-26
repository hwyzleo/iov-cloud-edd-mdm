package net.hwyz.iov.cloud.edd.mdm.service.application.port.service;

public interface IngestionAuthService {
    void authenticate(String sourceSystem, String authHeader);
}

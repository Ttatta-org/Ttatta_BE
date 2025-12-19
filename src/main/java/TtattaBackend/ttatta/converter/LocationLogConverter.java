package TtattaBackend.ttatta.converter;

import TtattaBackend.ttatta.domain.LocationLogs;
import TtattaBackend.ttatta.domain.Users;

public class LocationLogConverter {

    public static LocationLogs toLocationsLogs(Users user, String provisionalService, String recipient) {
        return LocationLogs.builder()
                .target(user.getId().toString())
                .provisionalService(provisionalService)
                .recipient(recipient)
                .build();
    }
}

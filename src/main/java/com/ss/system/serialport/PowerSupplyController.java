package com.ss.system.serialport;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/power")
@RequiredArgsConstructor
public class PowerSupplyController {
    private final PowerSupplyService powerSupplyService;
    
    @PostMapping("/initialize")
    public ResponseEntity<String> initialize() {
        try {
            powerSupplyService.setRemoteMode();
            return ResponseEntity.ok("Power supply initialized in remote mode");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
    
    @PostMapping("/voltage")
    public ResponseEntity<String> setVoltage(@RequestParam double voltage) {
        try {
            powerSupplyService.setVoltage(voltage);
            return ResponseEntity.ok("Voltage set to " + voltage + "V");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
    
    @PostMapping("/current")
    public ResponseEntity<String> setCurrent(@RequestParam double current) {
        try {
            powerSupplyService.setCurrent(current);
            return ResponseEntity.ok("Current set to " + current + "A");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
    
    @PostMapping("/output")
    public ResponseEntity<String> setOutput(@RequestParam boolean enable) {
        try {
            if (enable) {
                powerSupplyService.enableOutput();
                return ResponseEntity.ok("Output enabled");
            } else {
                powerSupplyService.disableOutput();
                return ResponseEntity.ok("Output disabled");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
    
    @GetMapping("/measurements")
    public ResponseEntity<Map<String, Double>> getMeasurements() {
        try {
            Map<String, Double> measurements = new HashMap<>();
            measurements.put("voltage", powerSupplyService.getVoltage());
            measurements.put("current", powerSupplyService.getCurrent());
            return ResponseEntity.ok(measurements);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}

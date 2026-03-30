package com.example.railwatcher.ui;

import com.example.railwatcher.config.RailwayWatcherProperties;
import com.example.railwatcher.service.AvailabilityPollingService;
import com.example.railwatcher.service.WatchJobService;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping
public class DashboardController {

    private final WatchJobService watchJobService;
    private final AvailabilityPollingService pollingService;
    private final RailwayWatcherProperties properties;

    public DashboardController(
            WatchJobService watchJobService,
            AvailabilityPollingService pollingService,
            RailwayWatcherProperties properties
    ) {
        this.watchJobService = watchJobService;
        this.pollingService = pollingService;
        this.properties = properties;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        return renderDashboard(model, new WatcherForm(), null);
    }

    @GetMapping("/watchers/{id}/edit")
    public String editWatcher(@PathVariable UUID id, Model model) {
        return renderDashboard(model, WatcherForm.from(watchJobService.get(id)), id);
    }

    @PostMapping("/ui/watchers")
    public String create(WatcherForm form, RedirectAttributes redirectAttributes) {
        watchJobService.create(form.toDomain(), properties.adminUser().id());
        redirectAttributes.addFlashAttribute("flashMessage", "Watcher created.");
        return "redirect:/dashboard";
    }

    @PostMapping("/ui/watchers/{id}")
    public String update(@PathVariable UUID id, WatcherForm form, RedirectAttributes redirectAttributes) {
        watchJobService.update(id, form.toDomain(), properties.adminUser().id());
        redirectAttributes.addFlashAttribute("flashMessage", "Watcher updated.");
        return "redirect:/dashboard";
    }

    @PostMapping("/ui/watchers/{id}/pause")
    public String pause(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        watchJobService.pause(id);
        redirectAttributes.addFlashAttribute("flashMessage", "Watcher paused.");
        return "redirect:/dashboard";
    }

    @PostMapping("/ui/watchers/{id}/resume")
    public String resume(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        watchJobService.resume(id);
        redirectAttributes.addFlashAttribute("flashMessage", "Watcher resumed.");
        return "redirect:/dashboard";
    }

    @PostMapping("/ui/watchers/{id}/check-now")
    public String checkNow(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        pollingService.pollNow(id);
        redirectAttributes.addFlashAttribute("flashMessage", "Manual check completed.");
        return "redirect:/dashboard";
    }

    @PostMapping("/ui/watchers/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        watchJobService.delete(id);
        redirectAttributes.addFlashAttribute("flashMessage", "Watcher deleted.");
        return "redirect:/dashboard";
    }

    private String renderDashboard(Model model, WatcherForm form, UUID editId) {
        var watchers = watchJobService.list();
        model.addAttribute("watchers", watchers);
        model.addAttribute("watcherForm", form);
        model.addAttribute("editId", editId);
        model.addAttribute("stations", properties.aliases().stations());
        model.addAttribute("trains", properties.aliases().trains());
        model.addAttribute("adminUser", properties.adminUser());
        return "dashboard";
    }
}

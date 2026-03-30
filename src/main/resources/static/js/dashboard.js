const themeKey = "railway-watcher-theme";
const themeToggle = document.getElementById("theme-toggle");

function applyTheme(theme) {
    if (theme === "dark") {
        document.body.setAttribute("data-theme", "dark");
    } else {
        document.body.removeAttribute("data-theme");
    }
}

const savedTheme = localStorage.getItem(themeKey);
if (savedTheme) {
    applyTheme(savedTheme);
}

if (themeToggle) {
    themeToggle.addEventListener("click", () => {
        const nextTheme = document.body.getAttribute("data-theme") === "dark" ? "light" : "dark";
        localStorage.setItem(themeKey, nextTheme);
        applyTheme(nextTheme);
    });
}

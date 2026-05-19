(function() {
    const saved = localStorage.getItem('theme');
    if (saved === 'dark') {
        document.documentElement.classList.add('dark');
    }
})();

async function syncThemeFromServer() {
    const res = await fetch('/api/user/me');
    if (!res.ok) return;
    const user = await res.json();
    const theme = user.theme || 'light';
    localStorage.setItem('theme', theme);
    document.documentElement.classList.toggle('dark', theme === 'dark');
}

async function toggleTheme() {
    const isDark = document.documentElement.classList.toggle('dark');
    const theme = isDark ? 'dark' : 'light';
    localStorage.setItem('theme', theme);
    await fetch(`/api/user/theme?theme=${theme}`, { method: 'PUT',
        headers: { [getCsrfHeader()]: getCsrfToken() }
    });
}

function getCsrfToken() {
    return document.querySelector('meta[name="_csrf"]')?.content;
}
function getCsrfHeader() {
    return document.querySelector('meta[name="_csrf_header"]')?.content;
}
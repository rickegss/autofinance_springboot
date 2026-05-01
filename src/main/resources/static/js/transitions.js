document.body.classList.add('loaded');

document.querySelectorAll('a[href]:not([href^="#"])').forEach(link => {
    link.addEventListener('click', e => {
        e.preventDefault();
        const href = link.href;
        document.body.style.opacity = '0';
        setTimeout(() => window.location.href = href, 300);
    });
});

document.querySelectorAll('form').forEach(form => {
    form.addEventListener('submit', () => {
        document.body.style.opacity = '0';
    });
});
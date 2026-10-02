async function runMatching() {
    const selected = document.querySelector('input[name="size"]:checked');

    const size = selected.value;

    const response = await fetch(`/api/run?size=${size}`);

    const data = await response.text();

    document.getElementById("matches").textContent = data;
}

async function showInput() {
    const selected = document.querySelector('input[name="size"]:checked');

    if (!selected) {
        return;
    }

    const size = selected.value;

    const response = await fetch(`/api/input?size=${size}`);
    const datas = await response.text();

    document.getElementById("input").textContent = datas;
}
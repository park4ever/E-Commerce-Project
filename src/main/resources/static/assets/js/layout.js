document.addEventListener("DOMContentLoaded", function () {
  const toggleBtn = document.getElementById("toggleBizInfo");
  const bizInfo = document.getElementById("bizInfoContent");
  const scrollBtn = document.getElementById("scrollToTop");

  if (toggleBtn && bizInfo) {
    toggleBtn.addEventListener("click", function () {
      bizInfo.classList.toggle("show");
      const isExpanded = bizInfo.classList.contains("show");
      toggleBtn.setAttribute("aria-expanded", isExpanded);
      toggleBtn.innerText = isExpanded ? "▲" : "▼";
    });
  }

  document.addEventListener("scroll", () => {
    if (scrollBtn) {
      if (window.scrollY > 300) {
        scrollBtn.classList.add("show");
      } else {
        scrollBtn.classList.remove("show");
      }
    }
  });
});
<template>
  <footer class="footer" role="contentinfo" aria-label="Pie de página" ref="footerRef">
    <div class="footer-inner">
      <nav class="footer-links" aria-label="Enlaces">
        <a href="#" class="link" @click="whoweare">Quienes somos</a>
      </nav>

      <div class="footer-copy">© <span>{{ year }}</span> CleanOps</div>
    </div>
  </footer>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'

const year = new Date().getFullYear()
const footerRef = ref(null)
const route = useRouter()

function updateBodyPadding() {
  if (!footerRef.value) return
  const h = footerRef.value.offsetHeight
  document.body.style.paddingBottom = `${h}px`
}

onMounted(() => {
  updateBodyPadding()
  window.addEventListener('resize', updateBodyPadding)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', updateBodyPadding)
  document.body.style.paddingBottom = ''
})

function whoweare () {
  route.push({name : 'whoweare'})
}

</script>

<style scoped>
.footer {
  position: fixed;
  left: 0;
  bottom: 0;
  width: 100%;
  z-index: 60;
  background: linear-gradient(180deg, #50593f, #3f4732);
  color: rgba(255,255,255,0.95);
  padding: 12px 16px;
  box-shadow: 0 -4px 12px rgba(0,0,0,0.12);
  border-top: 1px solid rgba(255,255,255,0.04);
}

.footer-inner {
  max-width: 1100px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.footer-links {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
  justify-content: center;
}

.link {
  color: rgba(255,255,255,0.9);
  text-decoration: none;
  padding: 4px 8px;
  border-radius: 6px;
  font-weight: 500;
}
.link:hover,
.link:focus {
  background: rgba(255,255,255,0.03);
  color: #dcd6c8;
  transform: translateY(-2px);
}

.sep {
  color: rgba(255,255,255,0.35);
  margin: 0 4px;
}

.footer-copy {
  color: rgba(255,255,255,0.75);
  font-size: 0.9rem;
}

/* Responsive */
@media (max-width: 600px) {
  .footer-inner {
    flex-direction: column;
    text-align: center;
    gap: 6px;
  }
}
</style>
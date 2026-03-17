<template>
  <section class="bg-[#f3f1ed]">
    <div class="mx-auto grid min-h-[640px] max-w-7xl items-center gap-10 px-4 py-12 lg:grid-cols-2 lg:py-0">
      <div class="max-w-xl">
        <p class="mb-5 text-sm font-bold uppercase tracking-[0.35em] text-[#e53637]">Summer Collection</p>
        <h1 class="mb-6 text-5xl font-semibold leading-tight text-black md:text-7xl">Fall - Winter Collections 2030</h1>
        <p class="mb-10 max-w-lg text-lg leading-8 text-neutral-600">
          A specialist label creating luxury essentials. Ethically crafted with an unwavering commitment to exceptional quality.
        </p>
        <div class="flex items-center gap-4">
          <router-link to="/shop" class="inline-flex items-center gap-3 bg-black px-8 py-4 text-sm font-semibold uppercase tracking-[0.25em] text-white transition hover:bg-neutral-800">
            Shop now
            <span aria-hidden="true">→</span>
          </router-link>
        </div>
      </div>

      <div class="relative flex min-h-[520px] items-end justify-center overflow-hidden">
        <div class="absolute top-16 h-[360px] w-[360px] rounded-full bg-[#f1dedd]"></div>
        <div class="absolute left-8 top-16 grid grid-cols-6 gap-4 opacity-50">
          <span v-for="dot in 42" :key="dot" class="h-2.5 w-2.5 rounded-full bg-white"></span>
        </div>
        <img src="/storefront/hero/hero-1.jpg" alt="Hero" class="relative z-10 max-h-[560px] w-auto object-contain" />
      </div>
    </div>
  </section>
</template>

import { Component, AfterViewInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatIcon } from '@angular/material/icon';
import { animate, stagger } from 'animejs';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, RouterModule, MatIcon],
  templateUrl: './landing.html',
  styleUrls: ['./landing.css']
})
export class LandingComponent implements AfterViewInit, OnDestroy {
  private observers: IntersectionObserver[] = [];

  ngAfterViewInit(): void {
    this.animateHero();
    this.animateScrollSections();
  }

  ngOnDestroy(): void {
    this.observers.forEach((o) => o.disconnect());
  }

  private animateHero(): void {
    const heroContent = document.querySelector('.hero-content');
    const heroCards = document.querySelectorAll('.hero-stat-card');
    const nav = document.querySelector('.landing-nav');

    if (nav) {
      animate(nav, { opacity: [0, 1], translateY: [-20, 0], duration: 500, ease: 'outQuad' });
    }

    if (heroContent) {
      animate(heroContent.children, {
        opacity: [0, 1],
        translateY: [30, 0],
        duration: 600,
        delay: stagger(120, { start: 200 }),
        ease: 'outQuad'
      });
    }

    if (heroCards.length) {
      animate(heroCards, {
        opacity: [0, 1],
        translateX: [50, 0],
        duration: 600,
        delay: stagger(120, { start: 400 }),
        ease: 'outQuad'
      });
    }
  }

  private animateScrollSections(): void {
    this.observe('.stats-bar', (el) => {
      const items = el.querySelectorAll('.stat-item');
      if (items.length) {
        animate(items, {
          opacity: [0, 1],
          translateY: [20, 0],
          duration: 500,
          delay: stagger(100),
          ease: 'outQuad'
        });
      }
    });

    this.observe('.how-it-works', (el) => {
      const title = el.querySelector('.section-title');
      const subtitle = el.querySelector('.section-subtitle');
      const cards = el.querySelectorAll('.step-card');
      if (title) animate(title, { opacity: [0, 1], translateY: [20, 0], duration: 500, ease: 'outQuad' });
      if (subtitle) animate(subtitle, { opacity: [0, 1], translateY: [20, 0], duration: 500, delay: 100, ease: 'outQuad' });
      if (cards.length) {
        animate(cards, {
          opacity: [0, 1],
          scale: [0.9, 1],
          translateY: [20, 0],
          duration: 500,
          delay: stagger(100, { start: 200 }),
          ease: 'outQuad'
        });
      }
    });

    this.observe('.benefits', (el) => {
      const title = el.querySelector('.section-title');
      const subtitle = el.querySelector('.section-subtitle');
      const cards = el.querySelectorAll('.benefit-card');
      if (title) animate(title, { opacity: [0, 1], translateY: [20, 0], duration: 500, ease: 'outQuad' });
      if (subtitle) animate(subtitle, { opacity: [0, 1], translateY: [20, 0], duration: 500, delay: 100, ease: 'outQuad' });
      if (cards.length) {
        animate(cards, {
          opacity: [0, 1],
          scale: [0.9, 1],
          translateY: [20, 0],
          duration: 500,
          delay: stagger(80, { start: 200 }),
          ease: 'outQuad'
        });
      }
    });

    this.observe('.cta', (el) => {
      const content = el.querySelector('.cta-content');
      if (content) {
        animate(content.children, {
          opacity: [0, 1],
          scale: [0.9, 1],
          translateY: [20, 0],
          duration: 600,
          delay: stagger(100),
          ease: 'outQuad'
        });
      }
    });

    this.observe('.landing-footer', (el) => {
      animate(el, { opacity: [0, 1], duration: 500, ease: 'outQuad' });
    });
  }

  private observe(selector: string, callback: (el: Element) => void): void {
    const el = document.querySelector(selector);
    if (!el) return;
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            callback(entry.target);
            observer.unobserve(entry.target);
          }
        });
      },
      { threshold: 0.15 }
    );
    observer.observe(el);
    this.observers.push(observer);
  }
}

import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { Subscription } from 'rxjs';
import { FaqService } from '../../../services/faq.service';

interface FaqItem {
  question: string;
  answer: string;
  open: boolean;
}

@Component({
  selector: 'app-faq-modal',
  standalone: true,
  imports: [CommonModule, MatIcon],
  templateUrl: './faq-modal.html',
  styleUrls: ['./faq-modal.css']
})
export class FaqModalComponent implements OnInit, OnDestroy {
  isOpen = false;
  private sub!: Subscription;

  faqs: FaqItem[] = [
    {
      question: '¿Qué es Bloodo.net?',
      answer: 'Bloodo.net es una plataforma web que conecta donadores de sangre con organizadores de campañas de donación. Permite a los organizadores crear y administrar campañas, y a los donadores encontrar y suscribirse a campañas cercanas.',
      open: false
    },
    {
      question: '¿Cómo me registro como donador?',
      answer: 'Hacé clic en "Registrarse" en la página de inicio de sesión, completá tus datos personales (nombre, email, teléfono, documento, tipo de sangre, etc.) y seleccioná el rol "Donador". Es rápido y gratuito.',
      open: false
    },
    {
      question: '¿Cómo me registro como organizador?',
      answer: 'Al registrarte, elegí el rol "Organizador" en el segundo paso. Vas a poder crear campañas de donación, administrar suscriptos y ver métricas de tus campañas.',
      open: false
    },
    {
      question: '¿Cómo me suscribo a una campaña?',
      answer: 'Entrá a la sección "Campañas", encontrá una campaña que te interese y hacé clic en "Ver Detalle". Ahí vas a encontrar un botón de "Suscribirme" que te conectará con el organizador.',
      open: false
    },
    {
      question: '¿Puedo cancelar mi suscripción a una campaña?',
      answer: 'Sí, podés cancelar tu suscripción en cualquier momento desde tu perfil de donante en la sección "Mis Donaciones".',
      open: false
    },
    {
      question: '¿Cómo creo una campaña?',
      answer: 'Si tenés rol de organizador, andá a "Mis Campañas" y hacé clic en "Nueva Campañas". Completá el título, descripción, fechas, horario, ubicación y tipo de sangre requerido (opcional).',
      open: false
    },
    {
      question: '¿Cómo finalizo una campaña?',
      answer: 'En la sección "Mis Campañas", seleccioná la campaña que querés finalizar y hacé clic en el botón de finalizar (ícono de check). Se te pedirá una fecha de finalización.',
      open: false
    },
    {
      question: '¿Qué información ven los organizadores de mí?',
      answer: 'Cuando te suscribís a una campaña, el organizador puede ver tu nombre, email y teléfono de contacto para coordinar la donación. Tu documento y otros datos sensibles no se comparten.',
      open: false
    },
    {
      question: '¿Bloodo.net es gratuito?',
      answer: 'Sí, Bloodo.net es completamente gratuito tanto para donadores como para organizadores. No se realizan cobros por usar la plataforma.',
      open: false
    },
    {
      question: '¿Mis datos están seguros?',
      answer: 'Bloodo.net protege tus datos personales y no los comparte con terceros sin tu consentimiento. Podés solicitar la eliminación de tus datos en cualquier momento contactándonos a soporte@bloodo.net.',
      open: false
    },
    {
      question: '¿Olvidé mi contraseña?',
      answer: 'En la página de inicio de sesión, hacé clic en "¿Olvidaste tu contraseña?" e ingresá tu email. Recibirás un código para restablecerla.',
      open: false
    },
    {
      question: '¿Cómo contacto al soporte?',
      answer: 'Podés escribirnos a soporte@bloodo.net con tu consulta. Te responderemos a la brevedad.',
      open: false
    }
  ];

  constructor(private faqService: FaqService) {}

  ngOnInit(): void {
    this.sub = this.faqService.isOpen$.subscribe(open => this.isOpen = open);
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  toggleFaq(index: number): void {
    this.faqs[index].open = !this.faqs[index].open;
  }

  close(): void {
    this.faqService.close();
    this.faqs.forEach(f => f.open = false);
  }

  onBackdropClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('faq-overlay')) {
      this.close();
    }
  }
}

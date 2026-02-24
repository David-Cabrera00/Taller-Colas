// Caso real: Turnos en recepción de un gimnasio (FIFO / Cola)

type Tipo = "CONGELAR" | "DESCONGELAR" | "ACTUALIZAR";

class Cola<T> {
  private items: T[] = [];
  enqueue(x: T) { this.items.push(x); }
  dequeue() { return this.items.shift(); }
  front() { return this.items[0]; }
  size() { return this.items.length; }
  rear() { return this.items[this.items.length - 1]; }
}

type Turno = { id: number; cliente: string; tipo: Tipo };

const cola = new Cola<Turno>();

// Llegan turnos (se encolan los usuarios)
cola.enqueue({ id: 1, cliente: "Ana Suarez", tipo: "CONGELAR" });
cola.enqueue({ id: 2, cliente: "Luis Diaz", tipo: "ACTUALIZAR" });
cola.enqueue({ id: 3, cliente: "María Mavi", tipo: "DESCONGELAR" });

console.log("Siguiente:", cola.front());
console.log("Pendientes:", cola.size());

// Se atiende en orden (FIFO)
console.log("Atendido:", cola.dequeue());
console.log("Atendido:", cola.dequeue());

console.log("Siguiente:", cola.front());
console.log("Pendientes:", cola.size());